package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.dtos.ResultResponseDto;
import com.fitgeek.IATestPreparator.dtos.SessionResponseDto;
import com.fitgeek.IATestPreparator.dtos.SubmitSessionRequestDto;
import com.fitgeek.IATestPreparator.entities.*;
import com.fitgeek.IATestPreparator.entities.enums.SessionStatus;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.repositories.QuizRepository;
import com.fitgeek.IATestPreparator.repositories.QuizSessionRepository;
import com.fitgeek.IATestPreparator.repositories.UserRepository;
import com.fitgeek.IATestPreparator.services.QuizSessionService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizSessionServiceImpl implements QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    //------------------------------------------------------
    // Create or Resume Session
    //------------------------------------------------------
    @Override
    @Transactional
    public SessionResponseDto getOrCreateSession(UserDetails userDetails, Long quizId) {

        User user = getUser(userDetails);

        Quiz quiz = quizRepository
                .findByIdAndOwnerId(quizId, user.getId())
                .orElseThrow(() -> new BusinessException("Quiz not found or access denied"));

        return quizSessionRepository
                .findActiveSession(user.getId(), quiz.getId())
                .map(session -> new SessionResponseDto(session.getId()))
                .orElseGet(() -> createNewSession(user, quiz));
    }

    private SessionResponseDto createNewSession(User user, Quiz quiz) {

        QuizSession session = QuizSession.builder()
                .user(user)
                .quiz(quiz)
                .status(SessionStatus.CREATED)
                .build();

        QuizSession saved = quizSessionRepository.save(session);

        quiz.setNumberOfSessions(quiz.getNumberOfSessions() + 1);

        return new SessionResponseDto(saved.getId());
    }

    //------------------------------------------------------
    // Submit Session
    //------------------------------------------------------

    @Override
    @Transactional
    public ResultResponseDto submitSession(
            UserDetails userDetails,
            Long sessionId,
            SubmitSessionRequestDto request
    ) {

        User user = getUser(userDetails);

        QuizSession session = quizSessionRepository
                .findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new BusinessException("Session not found or access denied"));

        validateSessionState(session);
        validateAnswerCount(session, request);

        int correctCount = processAnswers(session, request);

        long roundedRate = calculateScore(correctCount, session.getQuiz().getQuestions().size());

        long duration = Duration.between(session.getStartedAt(), LocalDateTime.now())
                .getSeconds();

        finalizeSession(session, correctCount, roundedRate, duration);

        return new ResultResponseDto(
                correctCount,
                session.getTotalQuestions(),
                roundedRate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponseDto getSessionById(Long sessionId, UserDetails userDetails) {

        User user = getUser(userDetails);

        QuizSession session = quizSessionRepository
                .findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new BusinessException("Session not found or access denied"));

        return mapToSessionDto(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponseDto> getAllSessionsByOwner(UserDetails userDetails) {

        User user = getUser(userDetails);

        List<QuizSession> sessions =
                quizSessionRepository.findAllByUserId(user.getId());

        return sessions.stream()
                .map(this::mapToSessionDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteSession(Long sessionId, UserDetails userDetails) {

        User user = getUser(userDetails);

        int deleted = quizSessionRepository
                .deleteByIdAndUserId(sessionId, user.getId());

        if (deleted == 0) {
            throw new BusinessException("Session not found or access denied");
        }
    }


    //------------------------------------------------------
    // Internal Logic
    //------------------------------------------------------

    private int processAnswers(QuizSession session, SubmitSessionRequestDto request) {

        List<Question> questions = session.getQuiz().getQuestions();
        int correctCount = 0;

        for (int i = 0; i < questions.size(); i++) {

            Question question = questions.get(i);
            Integer selectedIndex = request.answers().get(i);

            boolean isCorrect = selectedIndex != null
                    && selectedIndex.equals(question.getCorrectIndex());

            if (isCorrect) correctCount++;

            QuizAnswer answer = QuizAnswer.builder()
                    .session(session)
                    .question(question)
                    .selectedIndex(selectedIndex)
                    .correct(isCorrect)
                    .build();

            session.getAnswers().add(answer);
        }

        return correctCount;
    }

    private void finalizeSession(
            QuizSession session,
            int correctCount,
            long score,
            long duration
    ) {

        session.setCorrectCount(correctCount);
        session.setTotalQuestions(session.getQuiz().getQuestions().size());
        session.setScorePercentage(score);
        session.setDurationInSeconds(duration);
        session.setCompletedAt(LocalDateTime.now());
        session.setStatus(SessionStatus.COMPLETED);
    }

    private void validateSessionState(QuizSession session) {

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new BusinessException("Session already submitted");
        }
    }

    private void validateAnswerCount(
            QuizSession session,
            SubmitSessionRequestDto request
    ) {

        int expected = session.getQuiz().getQuestions().size();
        int received = request.answers().size();

        if (expected != received) {
            throw new BusinessException("Answer count mismatch");
        }
    }

    private long calculateScore(int correct, int total) {

        if (total == 0) return 0;

        double rate = (correct * 100.0) / total;
        return Math.round(rate);
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private SessionResponseDto mapToSessionDto(QuizSession session) {

        return new SessionResponseDto(
                session.getId()
        );
    }
}