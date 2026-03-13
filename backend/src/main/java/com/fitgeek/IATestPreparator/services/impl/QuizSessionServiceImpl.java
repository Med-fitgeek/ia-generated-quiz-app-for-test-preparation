package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.dtos.*;
import com.fitgeek.IATestPreparator.entities.*;
import com.fitgeek.IATestPreparator.entities.enums.SessionStatus;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.repositories.QuizReportRepository;
import com.fitgeek.IATestPreparator.repositories.QuizRepository;
import com.fitgeek.IATestPreparator.repositories.QuizSessionRepository;
import com.fitgeek.IATestPreparator.services.CurrentUserService;
import com.fitgeek.IATestPreparator.services.QuizReportService;
import com.fitgeek.IATestPreparator.services.QuizSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizSessionServiceImpl implements QuizSessionService {

    private static final Logger log =
            LoggerFactory.getLogger(QuizSessionServiceImpl.class);

    private final QuizSessionRepository quizSessionRepository;
    private final QuizRepository quizRepository;
    private final CurrentUserService currentUserService;
    private final QuizReportService quizReportService;
    private final QuizReportRepository quizReportRepository;

    //------------------------------------------------------
    // Create or Resume Session
    //------------------------------------------------------
    @Override
    @Transactional
    public SessionResponseDto getOrCreateSession(Long quizId) {

        User owner = currentUserService.getCurrentUser();

        Quiz quiz = quizRepository
                .findByIdAndOwnerId(quizId, owner.getId())
                .orElseThrow(() -> new BusinessException("Quiz not found or access denied", HttpStatus.NOT_FOUND));

        return quizSessionRepository
                .findActiveSession(owner.getId(), quiz.getId())
                .map(session -> new SessionResponseDto(
                        session.getId(),
                        session.getStatus(),
                        session.getTotalQuestions(),
                        session.getRate()
                ))
                .orElseGet(() -> createNewSession(owner, quiz));
    }

    private SessionResponseDto createNewSession(User user, Quiz quiz) {

        QuizSession session = QuizSession.builder()
                .user(user)
                .quiz(quiz)
                .status(SessionStatus.CREATED)
                .build();

        QuizSession saved = quizSessionRepository.save(session);
        log.info(
                "Quiz session created: sessionId={} userId={} quizId={}",
                saved.getId(),
                user.getId(),
                quiz.getId()
        );

        quiz.setNumberOfSessions(quiz.getNumberOfSessions() + 1);

        return new SessionResponseDto(
                saved.getId(),
                saved.getStatus(),
                saved.getTotalQuestions(),
                saved.getRate()

        );
    }

    //------------------------------------------------------
    // Submit Session
    //------------------------------------------------------

    @Override
    @Transactional
    public ResultResponseDto submitSession(Long sessionId, SubmitSessionRequestDto request) {

        User owner = currentUserService.getCurrentUser();

        QuizSession session = quizSessionRepository
                .findByIdAndUserId(sessionId, owner.getId())
                .orElseThrow(() -> new BusinessException("Session not found or access denied", HttpStatus.NOT_FOUND));

        validateSessionState(session);
        validateAnswerCount(session, request);

        int correctCount = processAnswers(session, request);

        long roundedRate = calculateScore(correctCount, session.getQuiz().getQuestions().size());

        long duration = Duration.between(session.getStartedAt(), LocalDateTime.now())
                .getSeconds();

        finalizeSession(session, correctCount, roundedRate, duration);
        QuizReport report = quizReportService.generateReport(session);
        quizReportRepository.save(report);

        log.info(
                "Quiz session submitted: sessionId={} userId={} score={}%",
                session.getId(),
                owner.getId(),
                roundedRate
        );

        return new ResultResponseDto(
                roundedRate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponseDto getSessionById(Long sessionId) {

        User owner = currentUserService.getCurrentUser();

        QuizSession session = quizSessionRepository
                .findByIdAndUserId(sessionId, owner.getId())
                .orElseThrow(() -> new BusinessException("Session not found or access denied", HttpStatus.NOT_FOUND));

        return mapToSessionDto(session);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponseDto> getAllSessionsByOwner(Pageable pageable) {

        User owner = currentUserService.getCurrentUser();

        Page<QuizSession> sessions =
                quizSessionRepository.findAllByUserId(owner.getId(), pageable);

        return sessions.map(this::mapToSessionDto);
    }

    @Override
    @Transactional
    public void deleteSession(Long sessionId) {

        User owner = currentUserService.getCurrentUser();

        int deleted = quizSessionRepository
                .deleteByIdAndUserId(sessionId, owner.getId());

        if (deleted == 0)
            throw new BusinessException("Session not found or access denied", HttpStatus.NOT_FOUND);
    }

    @Override
    public QuizReviewDto getSessionResult(Long sessionId) {

        User owner = currentUserService.getCurrentUser();

        QuizSession session = quizSessionRepository
                .findByIdAndUserId(sessionId, owner.getId())
                .orElseThrow(() -> new BusinessException("Session not found", HttpStatus.NOT_FOUND));

        Quiz quiz = session.getQuiz();

        List<QuestionReviewDto> questions = quiz.getQuestions()
                .stream()
                .map(question -> {

                    QuizAnswer answer = session.getAnswers()
                            .stream()
                            .filter(a -> a.getQuestion().getId().equals(question.getId()))
                            .findFirst()
                            .orElse(null);

                    int userAnswer = answer != null ? answer.getSelectedIndex(): -1;

                    return mapToQuestionReviewDto(question, userAnswer);

                })
                .toList();

        return new QuizReviewDto(
                session.getRate(),
                questions);
    }


    @Transactional(readOnly = true)
    @Override
    public QuizReportDto getSessionReport(Long sessionId) {

        User owner = currentUserService.getCurrentUser();

        QuizSession session = quizSessionRepository
                .findByIdAndUserId(sessionId, owner.getId())
                .orElseThrow(() ->
                        new BusinessException(
                                "Session not found",
                                HttpStatus.NOT_FOUND));

        QuizReport report = quizReportRepository
                .findBySessionId(sessionId)
                .orElseThrow(() ->
                        new BusinessException(
                                "Report not found",
                                HttpStatus.NOT_FOUND));

        return new QuizReportDto(
                report.getRate(),
                report.getCorrectAnswers(),
                report.getTotalQuestions(),
                report.getRecommendations()
        );
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
        session.setRate(score);
        session.setDurationInSeconds(duration);
        session.setCompletedAt(LocalDateTime.now());
        session.setStatus(SessionStatus.COMPLETED);
    }

    private void validateSessionState(QuizSession session) {

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new BusinessException("Session already submitted", HttpStatus.CONFLICT);
        }
    }

    private void validateAnswerCount(
            QuizSession session,
            SubmitSessionRequestDto request
    ) {

        int expected = session.getQuiz().getQuestions().size();
        int received = request.answers().size();

        if (expected != received) {
            throw new BusinessException("Answer count mismatch", HttpStatus.BAD_REQUEST);
        }
    }

    private long calculateScore(int correct, int total) {

        if (total == 0) return 0;

        double rate = (correct * 100.0) / total;
        return Math.round(rate);
    }

    private SessionResponseDto mapToSessionDto(QuizSession session) {

        return new SessionResponseDto(
                session.getId(),
                session.getStatus(),
                session.getTotalQuestions(),
                session.getRate()

        );
    }

    private QuestionReviewDto mapToQuestionReviewDto(
            Question question,
            int userAnswer
    ) {

        return new QuestionReviewDto(
                question.getStatement(),
                question.getChoices(),
                question.getCorrectIndex(),
                userAnswer,
                question.getExplanation()
        );
    }
}