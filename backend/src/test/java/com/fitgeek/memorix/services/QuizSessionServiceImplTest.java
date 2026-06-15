package com.fitgeek.memorix.services;

import com.fitgeek.memorix.dtos.*;
import com.fitgeek.memorix.entities.*;
import com.fitgeek.memorix.entities.enums.SessionStatus;
import com.fitgeek.memorix.excpetion.BusinessException;
import com.fitgeek.memorix.repositories.QuizReportRepository;
import com.fitgeek.memorix.repositories.QuizRepository;
import com.fitgeek.memorix.repositories.QuizSessionRepository;
import com.fitgeek.memorix.services.CurrentUserService;
import com.fitgeek.memorix.services.QuizReportService;
import com.fitgeek.memorix.services.impl.QuizSessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizSessionServiceImplTest {

    @Mock private QuizSessionRepository quizSessionRepository;
    @Mock private QuizRepository quizRepository;
    @Mock private CurrentUserService currentUserService;
    @Mock private QuizReportService quizReportService;
    @Mock private QuizReportRepository quizReportRepository;

    @InjectMocks
    private QuizSessionServiceImpl quizSessionService;

    private User owner;
    private Quiz quiz;
    private Question question1;
    private Question question2;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);

        question1 = Question.builder().build();
        setField(question1, "id", 100L);
        setField(question1, "statement", "Q1");
        setField(question1, "choices", List.of("A", "B", "C"));
        setField(question1, "correctIndex", 1);
        setField(question1, "explanation", "Because B is correct");

        question2 = Question.builder().build();
        setField(question2, "id", 200L);
        setField(question2, "statement", "Q2");
        setField(question2, "choices", List.of("A", "B"));
        setField(question2, "correctIndex", 0);
        setField(question2, "explanation", "Because A is correct");

        quiz = Quiz.builder().build();
        setField(quiz, "id", 10L);
        setField(quiz, "questions", new ArrayList<>(List.of(question1, question2)));
        setField(quiz, "numberOfSessions", 0L);
    }

    // ---------------------------------------------------
    // getOrCreateSession
    // ---------------------------------------------------

    @Test
    void getOrCreateSession_quizNotFound_throwsBusinessException() {
        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizRepository.findByIdAndOwnerId(10L, owner.getId())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> quizSessionService.getOrCreateSession(10L));

        assertEquals("Quiz not found or access denied", ex.getMessage());
    }

    @Test
    void getOrCreateSession_activeSessionExists_returnsExistingSession() {
        QuizSession active = QuizSession.builder()
                .user(owner)
                .quiz(quiz)
                .status(SessionStatus.CREATED)
                .build();
        setField(active, "id", 50L);
        setField(active, "totalQuestions", 2);
        setField(active, "rate", 0L);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizRepository.findByIdAndOwnerId(10L, owner.getId())).thenReturn(Optional.of(quiz));
        when(quizSessionRepository.findActiveSession(owner.getId(), quiz.getId()))
                .thenReturn(Optional.of(active));

        SessionResponseDto result = quizSessionService.getOrCreateSession(10L);

        assertEquals(50L, result.id());
        verify(quizSessionRepository, never()).save(any());
    }

    @Test
    void getOrCreateSession_noActiveSession_createsNewSession() {
        QuizSession saved = QuizSession.builder()
                .user(owner)
                .quiz(quiz)
                .status(SessionStatus.CREATED)
                .build();
        setField(saved, "id", 60L);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizRepository.findByIdAndOwnerId(10L, owner.getId())).thenReturn(Optional.of(quiz));
        when(quizSessionRepository.findActiveSession(owner.getId(), quiz.getId()))
                .thenReturn(Optional.empty());
        when(quizSessionRepository.save(any(QuizSession.class))).thenReturn(saved);

        SessionResponseDto result = quizSessionService.getOrCreateSession(10L);

        assertEquals(60L, result.id());
        assertEquals(1L, quiz.getNumberOfSessions());
        verify(quizSessionRepository).save(argThat(s ->
                s.getUser().equals(owner)
                        && s.getQuiz().equals(quiz)
                        && s.getStatus() == SessionStatus.CREATED
        ));
    }

    // ---------------------------------------------------
    // submitSession
    // ---------------------------------------------------

    @Test
    void submitSession_sessionNotFound_throwsBusinessException() {
        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(99L, owner.getId())).thenReturn(Optional.empty());

        SubmitSessionRequestDto request = new SubmitSessionRequestDto(List.of(0, 1));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> quizSessionService.submitSession(99L, request));

        assertEquals("Session not found or access denied", ex.getMessage());
    }

    @Test
    void submitSession_alreadyCompleted_throwsConflict() {
        QuizSession session = buildSession(SessionStatus.COMPLETED);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(session.getId(), owner.getId()))
                .thenReturn(Optional.of(session));

        SubmitSessionRequestDto request = new SubmitSessionRequestDto(List.of(1, 0));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> quizSessionService.submitSession(session.getId(), request));

        assertEquals("Session already submitted", ex.getMessage());
    }

    @Test
    void submitSession_answerCountMismatch_throwsBadRequest() {
        QuizSession session = buildSession(SessionStatus.CREATED);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(session.getId(), owner.getId()))
                .thenReturn(Optional.of(session));

        SubmitSessionRequestDto request = new SubmitSessionRequestDto(List.of(1));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> quizSessionService.submitSession(session.getId(), request));

        assertEquals("Answer count mismatch", ex.getMessage());
    }

    @Test
    void submitSession_validRequest_processesAnswersAndGeneratesReport() {
        QuizSession session = buildSession(SessionStatus.CREATED);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(session.getId(), owner.getId()))
                .thenReturn(Optional.of(session));

        QuizReport report = QuizReport.builder().build();
        when(quizReportService.generateReport(session)).thenReturn(report);

        // question1 correctIndex=1, question2 correctIndex=0 -> both correct
        SubmitSessionRequestDto request = new SubmitSessionRequestDto(List.of(1, 0));

        ResultResponseDto result = quizSessionService.submitSession(session.getId(), request);

        assertNotNull(result);
        assertEquals(100L, result.rate());

        assertEquals(2, session.getAnswers().size());
        assertEquals(2, session.getCorrectCount());
        assertEquals(2, session.getTotalQuestions());
        assertEquals(100L, session.getRate());
        assertEquals(SessionStatus.COMPLETED, session.getStatus());
        assertNotNull(session.getCompletedAt());

        verify(quizReportRepository).save(report);
    }

    @Test
    void submitSession_partiallyCorrectAnswers_calculatesRoundedRate() {
        QuizSession session = buildSession(SessionStatus.CREATED);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(session.getId(), owner.getId()))
                .thenReturn(Optional.of(session));

        QuizReport report = QuizReport.builder().build();
        when(quizReportService.generateReport(session)).thenReturn(report);

        // question1 correctIndex=1 -> answer 0 (wrong); question2 correctIndex=0 -> answer 0 (correct)
        SubmitSessionRequestDto request = new SubmitSessionRequestDto(List.of(0, 0));

        ResultResponseDto result = quizSessionService.submitSession(session.getId(), request);

        assertEquals(50L, result.rate());
        assertEquals(1, session.getCorrectCount());
    }


    // ---------------------------------------------------
    // getSessionById
    // ---------------------------------------------------

    @Test
    void getSessionById_found_returnsMappedDto() {
        QuizSession session = buildSession(SessionStatus.COMPLETED);
        setField(session, "rate", 75L);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(session.getId(), owner.getId()))
                .thenReturn(Optional.of(session));

        SessionResponseDto result = quizSessionService.getSessionById(session.getId());

        assertEquals(session.getId(), result.id());
        assertEquals(SessionStatus.COMPLETED, result.status());
        assertEquals(75L, result.rate());
    }

    @Test
    void getSessionById_notFound_throwsBusinessException() {
        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(999L, owner.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> quizSessionService.getSessionById(999L));
    }

    // ---------------------------------------------------
    // getAllSessionsByOwner
    // ---------------------------------------------------

    @Test
    void getAllSessionsByOwner_returnsMappedPage() {
        QuizSession session = buildSession(SessionStatus.COMPLETED);
        Pageable pageable = Pageable.unpaged();
        Page<QuizSession> page = new PageImpl<>(List.of(session));

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findAllByUserId(owner.getId(), pageable)).thenReturn(page);

        Page<SessionResponseDto> result = quizSessionService.getAllSessionsByOwner(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(session.getId(), result.getContent().get(0).id());
    }

    // ---------------------------------------------------
    // deleteSession
    // ---------------------------------------------------

    @Test
    void deleteSession_existingSession_deletesSuccessfully() {
        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.deleteByIdAndUserId(50L, owner.getId())).thenReturn(1);

        assertDoesNotThrow(() -> quizSessionService.deleteSession(50L));
    }

    @Test
    void deleteSession_nonExistingSession_throwsBusinessException() {
        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.deleteByIdAndUserId(50L, owner.getId())).thenReturn(0);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> quizSessionService.deleteSession(50L));

        assertEquals("Session not found or access denied", ex.getMessage());
    }

    // ---------------------------------------------------
    // getSessionResult
    // ---------------------------------------------------

    @Test
    void getSessionResult_returnsReviewWithUserAnswers() {
        QuizSession session = buildSession(SessionStatus.COMPLETED);
        setField(session, "rate", 50L);

        QuizAnswer answer1 = QuizAnswer.builder()
                .session(session)
                .question(question1)
                .selectedIndex(1)
                .correct(true)
                .build();

        session.getAnswers().add(answer1);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(session.getId(), owner.getId()))
                .thenReturn(Optional.of(session));

        QuizReviewDto result = quizSessionService.getSessionResult(session.getId());

        assertEquals(50L, result.rate());
        assertEquals(2, result.questions().size());

        QuestionReviewDto reviewQ1 = result.questions().get(0);
        assertEquals(1, reviewQ1.correctIndex());

        QuestionReviewDto reviewQ2 = result.questions().get(1);
        assertEquals(-1, reviewQ2.correctIndex());
    }

    @Test
    void getSessionResult_sessionNotFound_throwsBusinessException() {
        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(999L, owner.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> quizSessionService.getSessionResult(999L));
    }

    // ---------------------------------------------------
    // getSessionReport
    // ---------------------------------------------------

    @Test
    void getSessionReport_found_returnsReportDto() {
        QuizSession session = buildSession(SessionStatus.COMPLETED);

        QuizReport report = QuizReport.builder().build();
        setField(report, "rate", 80L);
        setField(report, "correctAnswers", 4);
        setField(report, "totalQuestions", 5);
        setField(report, "recommendations", "Review chapter 3");

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(session.getId(), owner.getId()))
                .thenReturn(Optional.of(session));
        when(quizReportRepository.findBySessionId(session.getId())).thenReturn(Optional.of(report));

        QuizReportDto result = quizSessionService.getSessionReport(session.getId());

        assertEquals(80L, result.rate());
        assertEquals(4, result.correctIndex());
        assertEquals(5, result.totalQuestions());
        assertEquals("Review chapter 3", result.recommendations());
    }

    @Test
    void getSessionReport_reportNotFound_throwsBusinessException() {
        QuizSession session = buildSession(SessionStatus.COMPLETED);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(session.getId(), owner.getId()))
                .thenReturn(Optional.of(session));
        when(quizReportRepository.findBySessionId(session.getId())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> quizSessionService.getSessionReport(session.getId()));

        assertEquals("Report not found", ex.getMessage());
    }

    @Test
    void getSessionReport_sessionNotFound_throwsBusinessException() {
        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(quizSessionRepository.findByIdAndUserId(999L, owner.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> quizSessionService.getSessionReport(999L));
    }

    // ---------------------------------------------------
    // Helpers
    // ---------------------------------------------------

    private QuizSession buildSession(SessionStatus status) {
        QuizSession session = QuizSession.builder()
                .user(owner)
                .quiz(quiz)
                .status(status)
                .build();
        setField(session, "id", 50L);
        setField(session, "answers", new ArrayList<>());
        setField(session, "startedAt", LocalDateTime.now().minusMinutes(5));
        return session;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }
}