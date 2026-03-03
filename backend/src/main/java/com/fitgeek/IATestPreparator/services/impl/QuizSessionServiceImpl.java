package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.dtos.ResultResponseDto;
import com.fitgeek.IATestPreparator.dtos.SessionRequestDto;
import com.fitgeek.IATestPreparator.dtos.SessionResponseDto;
import com.fitgeek.IATestPreparator.dtos.SubmitSessionRequestDto;
import com.fitgeek.IATestPreparator.entities.*;
import com.fitgeek.IATestPreparator.entities.enums.SessionStatus;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.repositories.QuizRepository;
import com.fitgeek.IATestPreparator.repositories.QuizSessionRepository;
import com.fitgeek.IATestPreparator.repositories.UserRepository;
import com.fitgeek.IATestPreparator.services.QuizSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizSessionServiceImpl implements QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Override
    public SessionResponseDto getOrCreateSession(UserDetails userDetails, Long quizId) {

        User owner = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException("Quiz not found"));

        Optional<QuizSession> existing =
                quizSessionRepository.findActiveSession(owner.getId(), quiz.getId());

        if (existing.isPresent()) {
            return new SessionResponseDto(existing.get().getId());
        }

        QuizSession session = QuizSession.builder()
                .user(owner)
                .quiz(quiz)
                .status(SessionStatus.CREATED)
                .build();

        QuizSession savedSession = quizSessionRepository.save(session);

        return new SessionResponseDto(
                savedSession.getId()
        );
    }


    @Override
    public ResultResponseDto submitSession(
            UserDetails userDetails,
            Long sessionId,
            SubmitSessionRequestDto request
    ) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));

        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException("Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized session access");
        }

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new BusinessException("Session already submitted");
        }


        List<Question> questions = session.getQuiz().getQuestions();

        if (questions.size() != request.answers().size()) {
            throw new BusinessException("Answer count mismatch");
        }

        int correctCount = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Integer selectedIndex = request.answers().get(i);

            boolean isCorrect = selectedIndex != null &&
                    selectedIndex == question.getCorrectIndex();

            if (isCorrect) correctCount++;

            QuizAnswer answer = QuizAnswer.builder()
                    .session(session)
                    .question(question)
                    .selectedIndex(selectedIndex)
                    .correct(isCorrect)
                    .build();

            session.getAnswers().add(answer);
        }

        double rate = (correctCount * 100.0) / questions.size();
        long roundedRate = Math.round(rate);
        Duration duration = Duration.between(
                session.getStartedAt(),
                LocalDateTime.now()
        );

        session.setCorrectCount(correctCount);
        session.setTotalQuestions(questions.size());
        session.setScorePercentage(roundedRate);
        session.setDurationInSeconds(duration.getSeconds());
        session.setCompletedAt(LocalDateTime.now());
        session.setStatus(SessionStatus.COMPLETED);

        quizSessionRepository.save(session);

        return new ResultResponseDto(
                correctCount,
                questions.size(),
                roundedRate
        );
    }

}
