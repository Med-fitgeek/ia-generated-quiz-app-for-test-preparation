package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.Prompting.GenerationPromptStrategy;
import com.fitgeek.IATestPreparator.Utils.GeneratedQuizValidator;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuestionDto;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.dtos.QuizGenerationRequestDto;
import com.fitgeek.IATestPreparator.dtos.QuizResponseDto;
import com.fitgeek.IATestPreparator.entities.KnowledgeSource;
import com.fitgeek.IATestPreparator.entities.Question;
import com.fitgeek.IATestPreparator.entities.Quiz;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.entities.enums.Difficulty;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.repositories.KnowledgeSourceRepository;
import com.fitgeek.IATestPreparator.repositories.QuizRepository;
import com.fitgeek.IATestPreparator.services.CurrentUserService;
import com.fitgeek.IATestPreparator.services.QuizGenerationService;
import com.fitgeek.IATestPreparator.services.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Service
@RequiredArgsConstructor
public class QuizGenerationServiceImpl implements QuizGenerationService {

    private static final Logger log =
            LoggerFactory.getLogger(QuizGenerationServiceImpl.class);

    private final ChatClient chatClient;
    private final GenerationPromptStrategy promptStrategy;
    private final QuizRepository quizRepository;
    private final KnowledgeSourceRepository sourceRepository;
    private final CurrentUserService currentUserService;
    private final RateLimitService rateLimitService;

    @Override
    @Transactional
    public GeneratedQuizDto generateQuiz(QuizGenerationRequestDto requestDto) {

        User owner = currentUserService.getCurrentUser();
        rateLimitService.checkQuizGenerationLimit(owner.getId());

        log.info(
                "User {} requested quiz generation: sourceId={}, numberOfQuestions={}, difficulty={}",
                owner.getId(),
                requestDto.sourceId(),
                requestDto.numberOfQuestions(),
                requestDto.difficulty()
        );

        KnowledgeSource source = sourceRepository.findByIdAndOwnerId(requestDto.sourceId(), owner.getId())
                .orElseThrow(() -> new BusinessException("KnowledgeSource not found or access denied", HttpStatus.NOT_FOUND));

        String systemInstructions = promptStrategy.buildPrompt(
                requestDto.numberOfQuestions(),
                Difficulty.valueOf(requestDto.difficulty())
        );

        try {
            log.info("Calling AI to generate quiz...");
            long start = System.currentTimeMillis();
            GeneratedQuizDto generatedQuiz = chatClient.prompt()
                    .system(systemInstructions)
                    .user(source.getNormalizedContent())
                    .call()
                    .entity(GeneratedQuizDto.class);

            GeneratedQuizValidator.validate(
                    generatedQuiz,
                    requestDto.numberOfQuestions()
            );

            long duration = System.currentTimeMillis() - start;
            log.info("AI generation completed in {} ms", duration);

            Quiz quiz = buildQuiz(requestDto.title(), owner, source, generatedQuiz);

            Quiz savedQuiz = quizRepository.save(quiz);
            log.info(
                    "Quiz saved successfully: quizId={}, userId={}, questionCount={}",
                    savedQuiz.getId(),
                    owner.getId(),
                    savedQuiz.getQuestions().size()
            );

            return mapToDto(savedQuiz);

        } catch (Exception e) {
            log.error(
                    "AI quiz generation failed for user={} source={}",
                    owner.getId(),
                    source.getId(),
                    e
            );
            throw new BusinessException("AI quiz generation failed", HttpStatus.INTERNAL_SERVER_ERROR  );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GeneratedQuizDto getQuizById(Long quizId) {

        User owner = currentUserService.getCurrentUser();

        Quiz quiz = quizRepository
                .findByIdAndOwnerId(quizId, owner.getId())
                .orElseThrow(() -> new BusinessException("Quiz not found or access denied", HttpStatus.NOT_FOUND));

        return mapToDto(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizResponseDto> getAllQuizzesByOwner(Pageable pageable) {

        User owner = currentUserService.getCurrentUser();

        Page<Quiz> quizzes = quizRepository.findAllByOwnerId(owner.getId(), pageable);

        return quizzes
                .map(quiz -> new QuizResponseDto(
                        quiz.getId(),
                        quiz.getOwner().getId(),
                        quiz.getTitle(),
                        quiz.getGeneratedAt(),
                        quiz.getNumberOfSessions()
                ));
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {

        User owner = currentUserService.getCurrentUser();

        int deleted = quizRepository.deleteByIdAndOwnerId(quizId, owner.getId());

        if (deleted == 0) {
            throw new BusinessException("Quiz not found or access denied", HttpStatus.BAD_REQUEST);
        }
}


    //------------------------------------------------------
    // Internal Logic
    //------------------------------------------------------
    private Quiz buildQuiz(String title, User owner,
                           KnowledgeSource source,
                           GeneratedQuizDto generatedQuiz) {

        Quiz quiz = Quiz.builder()
                .title(title)
                .owner(owner)
                .numberOfSessions(0L)
                .sourceChecksum(source.getChecksum())
                .generatorVersion(promptStrategy.getClass().getSimpleName())
                .build();

        generatedQuiz.generatedQuestions().forEach(dto -> {
            Question question = Question.builder()
                    .statement(dto.statement())
                    .choices(dto.choices())
                    .correctIndex(dto.correctIndex())
                    .explanation(dto.explanation())
                    .sourceQuote(dto.sourceQuote())
                    .build();

            quiz.addQuestion(question);
        });

        return quiz;
    }

    private GeneratedQuizDto mapToDto(Quiz quiz) {

        List<GeneratedQuestionDto> questionDtos = quiz.getQuestions()
                .stream()
                .map(question -> GeneratedQuestionDto.builder()
                        .statement(question.getStatement())
                        .choices(question.getChoices())
                        .correctIndex(question.getCorrectIndex())
                        .explanation(question.getExplanation())
                        .sourceQuote(question.getSourceQuote())
                        .build())
                .toList();

        return new GeneratedQuizDto(quiz.getId(), questionDtos);
    }
}