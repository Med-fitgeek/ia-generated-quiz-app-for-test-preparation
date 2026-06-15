package com.fitgeek.memorix.services;

import com.fitgeek.memorix.Prompting.GenerationPromptStrategy;
import com.fitgeek.memorix.utils.GeneratedQuizValidator;
import com.fitgeek.memorix.dtos.GeneratedQuestionDto;
import com.fitgeek.memorix.dtos.GeneratedQuizDto;
import com.fitgeek.memorix.dtos.QuizGenerationRequestDto;
import com.fitgeek.memorix.dtos.QuizResponseDto;
import com.fitgeek.memorix.entities.KnowledgeSource;
import com.fitgeek.memorix.entities.Question;
import com.fitgeek.memorix.entities.Quiz;
import com.fitgeek.memorix.entities.User;
import com.fitgeek.memorix.entities.enums.Difficulty;
import com.fitgeek.memorix.excpetion.BusinessException;
import com.fitgeek.memorix.repositories.KnowledgeSourceRepository;
import com.fitgeek.memorix.repositories.QuizRepository;
import com.fitgeek.memorix.services.impl.QuizGenerationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuizGenerationServiceImpl")
class QuizGenerationServiceImplTest {

    // ------------------------------------------------------------------ mocks
    @Mock private ChatClient chatClient;
    @Mock private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock private ChatClient.CallResponseSpec callResponseSpec;
    @Mock private GenerationPromptStrategy promptStrategy;
    @Mock private QuizRepository quizRepository;
    @Mock private KnowledgeSourceRepository sourceRepository;
    @Mock private CurrentUserService currentUserService;
    @Mock private RateLimitService rateLimitService;

    @InjectMocks
    private QuizGenerationServiceImpl service;

    // ---------------------------------------------------------------- fixtures
    private User owner;
    private KnowledgeSource source;
    private QuizGenerationRequestDto requestDto;
    private GeneratedQuizDto aiResponse;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .email("user@test.com")
                .build();

        source = KnowledgeSource.builder()
                .id(10L)
                .normalizedContent("Some course content about Spring Boot...")
                .checksum("abc123")
                .owner(owner)
                .build();

        requestDto = new QuizGenerationRequestDto(
                10L,           // sourceId
                "My Quiz",     // title
                5,             // numberOfQuestions
                "MODERATE"       // difficulty
        );

        aiResponse = new GeneratedQuizDto(
                null,
                null,
                List.of(
                        buildQuestionDto("Q1", List.of("A", "B", "C", "D"), 0, "Exp1", "Quote1"),
                        buildQuestionDto("Q2", List.of("A", "B", "C", "D"), 1, "Exp2", "Quote2"),
                        buildQuestionDto("Q3", List.of("A", "B", "C", "D"), 2, "Exp3", "Quote3"),
                        buildQuestionDto("Q4", List.of("A", "B", "C", "D"), 3, "Exp4", "Quote4"),
                        buildQuestionDto("Q5", List.of("A", "B", "C", "D"), 0, "Exp5", "Quote5")
                )
        );
    }

    // =========================================================================
    // generateQuiz
    // =========================================================================
    @Nested
    @DisplayName("generateQuiz()")
    class GenerateQuiz {

        // ---- happy path ------------------------------------------------------

        @Test
        @DisplayName("should return GeneratedQuizDto when all inputs are valid")
        void shouldReturnGeneratedQuizDto_whenAllInputsAreValid() {
            // given
            stubHappyPath(5);

            Quiz savedQuiz = buildPersistedQuiz(42L, "My Quiz", 5);
            when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);

            // when
            GeneratedQuizDto result = service.generateQuiz(requestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.quizId()).isEqualTo(42L);
            assertThat(result.title()).isEqualTo("My Quiz");
            assertThat(result.generatedQuestions()).hasSize(5);
        }

        @Test
        @DisplayName("should persist a Quiz entity with the correct owner")
        void shouldPersistQuizWithCorrectOwner() {
            stubHappyPath(5);

            ArgumentCaptor<Quiz> quizCaptor = ArgumentCaptor.forClass(Quiz.class);
            Quiz savedQuiz = buildPersistedQuiz(1L, "My Quiz", 5);
            when(quizRepository.save(quizCaptor.capture())).thenReturn(savedQuiz);

            service.generateQuiz(requestDto);

            Quiz captured = quizCaptor.getValue();
            assertThat(captured.getOwner()).isEqualTo(owner);
            assertThat(captured.getTitle()).isEqualTo("My Quiz");
        }

        @Test
        @DisplayName("should persist exactly as many Question entities as AI returned")
        void shouldPersistCorrectNumberOfQuestions() {
            stubHappyPath(5);

            ArgumentCaptor<Quiz> quizCaptor = ArgumentCaptor.forClass(Quiz.class);
            Quiz savedQuiz = buildPersistedQuiz(1L, "My Quiz", 5);
            when(quizRepository.save(quizCaptor.capture())).thenReturn(savedQuiz);

            service.generateQuiz(requestDto);

            assertThat(quizCaptor.getValue().getQuestions()).hasSize(5);
        }

        @Test
        @DisplayName("should store source checksum on the saved quiz")
        void shouldStoreSourceChecksum() {
            stubHappyPath(5);

            ArgumentCaptor<Quiz> quizCaptor = ArgumentCaptor.forClass(Quiz.class);
            Quiz savedQuiz = buildPersistedQuiz(1L, "My Quiz", 5);
            when(quizRepository.save(quizCaptor.capture())).thenReturn(savedQuiz);

            service.generateQuiz(requestDto);

            assertThat(quizCaptor.getValue().getSourceChecksum()).isEqualTo("abc123");
        }

        @Test
        @DisplayName("should initialise numberOfSessions to 0 on the new quiz")
        void shouldInitialiseNumberOfSessionsToZero() {
            stubHappyPath(5);

            ArgumentCaptor<Quiz> quizCaptor = ArgumentCaptor.forClass(Quiz.class);
            Quiz savedQuiz = buildPersistedQuiz(1L, "My Quiz", 5);
            when(quizRepository.save(quizCaptor.capture())).thenReturn(savedQuiz);

            service.generateQuiz(requestDto);

            assertThat(quizCaptor.getValue().getNumberOfSessions()).isZero();
        }

        @Test
        @DisplayName("should call rateLimitService before any generation logic")
        void shouldCallRateLimitServiceFirst() {
            stubHappyPath(5);
            when(quizRepository.save(any())).thenReturn(buildPersistedQuiz(1L, "My Quiz", 5));

            service.generateQuiz(requestDto);

            verify(rateLimitService).checkQuizGenerationLimit(owner.getId());
        }

        @Test
        @DisplayName("should build prompt using requestDto difficulty and numberOfQuestions")
        void shouldBuildPromptWithCorrectParameters() {
            stubHappyPath(5);
            when(quizRepository.save(any())).thenReturn(buildPersistedQuiz(1L, "My Quiz", 5));

            service.generateQuiz(requestDto);

            verify(promptStrategy).buildPrompt(5, Difficulty.MODERATE);
        }

        @Test
        @DisplayName("should feed normalizedContent of the source to the AI")
        void shouldFeedNormalizedContentToAI() {
            stubHappyPath(5);
            when(quizRepository.save(any())).thenReturn(buildPersistedQuiz(1L, "My Quiz", 5));

            service.generateQuiz(requestDto);

            verify(requestSpec).user("Some course content about Spring Boot...");
        }

        // ---- access control -------------------------------------------------

        @Test
        @DisplayName("should throw NOT_FOUND when source does not belong to owner")
        void shouldThrowNotFound_whenSourceDoesNotBelongToOwner() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(sourceRepository.findByIdAndOwnerId(requestDto.sourceId(), owner.getId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.generateQuiz(requestDto))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }

        @Test
        @DisplayName("should throw NOT_FOUND when source id does not exist at all")
        void shouldThrowNotFound_whenSourceIdDoesNotExist() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(sourceRepository.findByIdAndOwnerId(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            QuizGenerationRequestDto badRequest = new QuizGenerationRequestDto(
                    999L, "title", 5, "MODERATE"
            );

            assertThatThrownBy(() -> service.generateQuiz(badRequest))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getStatus())
                            .isEqualTo(HttpStatus.NOT_FOUND));
        }

        // ---- rate limiting --------------------------------------------------

        @Test
        @DisplayName("should propagate exception thrown by rateLimitService")
        void shouldPropagateRateLimitException() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            doThrow(new BusinessException("Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS))
                    .when(rateLimitService).checkQuizGenerationLimit(owner.getId());

            assertThatThrownBy(() -> service.generateQuiz(requestDto))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getStatus())
                            .isEqualTo(HttpStatus.TOO_MANY_REQUESTS));

            // AI should never be called if rate limit blocks
            verifyNoInteractions(chatClient);
        }

        // ---- AI / validation failure ----------------------------------------

        @Test
        @DisplayName("should throw INTERNAL_SERVER_ERROR when AI call throws a runtime exception")
        void shouldThrowInternalError_whenAICallFails() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            doNothing().when(rateLimitService).checkQuizGenerationLimit(anyLong());
            when(sourceRepository.findByIdAndOwnerId(requestDto.sourceId(), owner.getId()))
                    .thenReturn(Optional.of(source));
            when(promptStrategy.buildPrompt(anyInt(), any(Difficulty.class))).thenReturn("prompt");

            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.system(anyString())).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenThrow(new RuntimeException("AI service unavailable"));

            assertThatThrownBy(() -> service.generateQuiz(requestDto))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getStatus())
                            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR));
        }

        @Test
        @DisplayName("should throw INTERNAL_SERVER_ERROR when GeneratedQuizValidator rejects the AI response")
        void shouldThrowInternalError_whenValidationFails() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            doNothing().when(rateLimitService).checkQuizGenerationLimit(anyLong());
            when(sourceRepository.findByIdAndOwnerId(requestDto.sourceId(), owner.getId()))
                    .thenReturn(Optional.of(source));
            when(promptStrategy.buildPrompt(anyInt(), any(Difficulty.class))).thenReturn("prompt");

            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.system(anyString())).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);

            // AI returns wrong number of questions → validator blows up
            GeneratedQuizDto invalidResponse = new GeneratedQuizDto(null, null, List.of());
            when(callResponseSpec.entity(GeneratedQuizDto.class)).thenReturn(invalidResponse);

            try (MockedStatic<GeneratedQuizValidator> validatorMock =
                         mockStatic(GeneratedQuizValidator.class)) {
                validatorMock.when(() -> GeneratedQuizValidator.validate(any(), anyInt()))
                        .thenThrow(new IllegalArgumentException("Wrong number of questions"));

                assertThatThrownBy(() -> service.generateQuiz(requestDto))
                        .isInstanceOf(BusinessException.class)
                        .satisfies(ex -> assertThat(((BusinessException) ex).getStatus())
                                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR));
            }
        }

        @Test
        @DisplayName("should NOT save quiz when AI call fails")
        void shouldNotSaveQuiz_whenAICallFails() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            doNothing().when(rateLimitService).checkQuizGenerationLimit(anyLong());
            when(sourceRepository.findByIdAndOwnerId(requestDto.sourceId(), owner.getId()))
                    .thenReturn(Optional.of(source));
            when(promptStrategy.buildPrompt(anyInt(), any(Difficulty.class))).thenReturn("prompt");
            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.system(anyString())).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenThrow(new RuntimeException("timeout"));

            assertThatThrownBy(() -> service.generateQuiz(requestDto));

            verifyNoInteractions(quizRepository);
        }

        // ---- helpers --------------------------------------------------------

        private void stubHappyPath(int numberOfQuestions) {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            doNothing().when(rateLimitService).checkQuizGenerationLimit(owner.getId());
            when(sourceRepository.findByIdAndOwnerId(requestDto.sourceId(), owner.getId()))
                    .thenReturn(Optional.of(source));
            when(promptStrategy.buildPrompt(numberOfQuestions, Difficulty.MODERATE)).thenReturn("system-prompt");
            // Wire up the ChatClient fluent chain
            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.system(anyString())).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.entity(GeneratedQuizDto.class)).thenReturn(aiResponse);
        }
    }

    // =========================================================================
    // getQuizById
    // =========================================================================
    @Nested
    @DisplayName("getQuizById()")
    class GetQuizById {

        @Test
        @DisplayName("should return mapped DTO when quiz belongs to current user")
        void shouldReturnDto_whenQuizBelongsToOwner() {
            Quiz quiz = buildPersistedQuiz(5L, "Spring Quiz", 3);
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findByIdAndOwnerId(5L, owner.getId())).thenReturn(Optional.of(quiz));

            GeneratedQuizDto result = service.getQuizById(5L);

            assertThat(result.quizId()).isEqualTo(5L);
            assertThat(result.title()).isEqualTo("Spring Quiz");
            assertThat(result.generatedQuestions()).hasSize(3);
        }

        @Test
        @DisplayName("should map all question fields correctly")
        void shouldMapAllQuestionFields() {
            Quiz quiz = buildPersistedQuiz(5L, "Spring Quiz", 1);
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findByIdAndOwnerId(5L, owner.getId())).thenReturn(Optional.of(quiz));

            GeneratedQuizDto result = service.getQuizById(5L);

            GeneratedQuestionDto q = result.generatedQuestions().get(0);
            assertThat(q.statement()).isEqualTo("Question 0");
            assertThat(q.choices()).containsExactly("A", "B", "C", "D");
            assertThat(q.correctIndex()).isZero();
            assertThat(q.explanation()).isEqualTo("Explanation 0");
            assertThat(q.sourceQuote()).isEqualTo("Quote 0");
        }

        @Test
        @DisplayName("should throw NOT_FOUND when quiz does not exist")
        void shouldThrowNotFound_whenQuizNotFound() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findByIdAndOwnerId(99L, owner.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getQuizById(99L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getStatus())
                            .isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        @DisplayName("should throw NOT_FOUND when quiz belongs to another user")
        void shouldThrowNotFound_whenQuizBelongsToAnotherUser() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            // repository returns empty because ownerId doesn't match
            when(quizRepository.findByIdAndOwnerId(5L, owner.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getQuizById(5L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // =========================================================================
    // getAllQuizzesByOwner
    // =========================================================================
    @Nested
    @DisplayName("getAllQuizzesByOwner()")
    class GetAllQuizzesByOwner {

        @Test
        @DisplayName("should return a page of QuizResponseDto for the current user")
        void shouldReturnPageOfDtos() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Quiz> quizzes = List.of(
                    buildPersistedQuiz(1L, "Quiz A", 3),
                    buildPersistedQuiz(2L, "Quiz B", 5)
            );
            Page<Quiz> page = new PageImpl<>(quizzes, pageable, 2);

            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findAllByOwnerId(owner.getId(), pageable)).thenReturn(page);

            Page<QuizResponseDto> result = service.getAllQuizzesByOwner(pageable);

            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent())
                    .extracting(QuizResponseDto::title)
                    .containsExactly("Quiz A", "Quiz B");
        }

        @Test
        @DisplayName("should return empty page when user has no quizzes")
        void shouldReturnEmptyPage_whenNoQuizzes() {
            Pageable pageable = PageRequest.of(0, 10);
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findAllByOwnerId(owner.getId(), pageable))
                    .thenReturn(Page.empty(pageable));

            Page<QuizResponseDto> result = service.getAllQuizzesByOwner(pageable);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map owner id into each QuizResponseDto")
        void shouldMapOwnerIdIntoDto() {
            Pageable pageable = PageRequest.of(0, 10);
            Quiz quiz = buildPersistedQuiz(7L, "Java Quiz", 2);
            Page<Quiz> page = new PageImpl<>(List.of(quiz));

            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findAllByOwnerId(owner.getId(), pageable)).thenReturn(page);

            Page<QuizResponseDto> result = service.getAllQuizzesByOwner(pageable);

            assertThat(result.getContent().get(0).ownerId()).isEqualTo(owner.getId());
        }

        @Test
        @DisplayName("should map numberOfSessions correctly")
        void shouldMapNumberOfSessions() {
            Pageable pageable = PageRequest.of(0, 10);
            Quiz quiz = buildPersistedQuiz(7L, "Java Quiz", 2);
            quiz.setNumberOfSessions(3L);
            Page<Quiz> page = new PageImpl<>(List.of(quiz));

            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findAllByOwnerId(owner.getId(), pageable)).thenReturn(page);

            Page<QuizResponseDto> result = service.getAllQuizzesByOwner(pageable);

            assertThat(result.getContent().get(0).numberOfSessions()).isEqualTo(3L);
        }

        @Test
        @DisplayName("should respect pageable parameters")
        void shouldPassPageableToRepository() {
            Pageable pageable = PageRequest.of(2, 5);
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findAllByOwnerId(owner.getId(), pageable))
                    .thenReturn(Page.empty(pageable));

            service.getAllQuizzesByOwner(pageable);

            verify(quizRepository).findAllByOwnerId(owner.getId(), pageable);
        }
    }

    // =========================================================================
    // deleteQuiz
    // =========================================================================
    @Nested
    @DisplayName("deleteQuiz()")
    class DeleteQuiz {

        @Test
        @DisplayName("should delete quiz when it belongs to the current user")
        void shouldDeleteQuiz_whenOwnerMatches() {
            Quiz quiz = buildPersistedQuiz(5L, "To Delete", 2);
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findByIdAndOwnerId(5L, owner.getId())).thenReturn(Optional.of(quiz));

            service.deleteQuiz(5L);

            verify(quizRepository).delete(quiz);
        }

        @Test
        @DisplayName("should throw NOT_FOUND when quiz does not exist")
        void shouldThrowNotFound_whenQuizNotFound() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findByIdAndOwnerId(99L, owner.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteQuiz(99L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getStatus())
                            .isEqualTo(HttpStatus.NOT_FOUND));

            verify(quizRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should throw NOT_FOUND when quiz belongs to another user")
        void shouldThrowNotFound_whenNotOwner() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findByIdAndOwnerId(5L, owner.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteQuiz(5L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getStatus())
                            .isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        @DisplayName("should not call delete when quiz is not found")
        void shouldNotCallDeleteWhenNotFound() {
            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(quizRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteQuiz(5L));

            verify(quizRepository, never()).delete(any(Quiz.class));
        }
    }

    // =========================================================================
    // Helper builders
    // =========================================================================

    private GeneratedQuestionDto buildQuestionDto(String statement,
                                                  List<String> choices,
                                                  int correctIndex,
                                                  String explanation,
                                                  String sourceQuote) {
        return GeneratedQuestionDto.builder()
                .statement(statement)
                .choices(choices)
                .correctIndex(correctIndex)
                .explanation(explanation)
                .sourceQuote(sourceQuote)
                .build();
    }

    /**
     * Builds a Quiz that simulates a JPA-persisted entity:
     * it already has an id, an owner, and N questions.
     */
    private Quiz buildPersistedQuiz(Long id, String title, int questionCount) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < questionCount; i++) {
            questions.add(Question.builder()
                    .statement("Question " + i)
                    .choices(List.of("A", "B", "C", "D"))
                    .correctIndex(i % 4)
                    .explanation("Explanation " + i)
                    .sourceQuote("Quote " + i)
                    .build());
        }

        return Quiz.builder()
                .id(id)
                .title(title)
                .owner(owner)
                .numberOfSessions(0L)
                .sourceChecksum("abc123")
                .generatorVersion("GenerationPromptV1Strategy")
                .generatedAt(LocalDateTime.now())
                .questions(questions)
                .build();
    }
}