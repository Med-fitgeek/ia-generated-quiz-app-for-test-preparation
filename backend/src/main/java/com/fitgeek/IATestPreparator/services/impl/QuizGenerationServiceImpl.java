package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.Prompting.PromptStrategy;
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
import com.fitgeek.IATestPreparator.repositories.UserRepository;
import com.fitgeek.IATestPreparator.services.QuizGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class QuizGenerationServiceImpl implements QuizGenerationService {

    private final ChatClient chatClient;
    private final PromptStrategy promptStrategy;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final KnowledgeSourceRepository sourceRepository;


    @Override
    @Transactional
    public GeneratedQuizDto generateQuiz(QuizGenerationRequestDto requestDto,
                                         UserDetails userDetails) {

        User owner = getUser(userDetails);

        KnowledgeSource source = sourceRepository.findByIdAndOwnerId(requestDto.sourceId(), owner.getId())
                .orElseThrow(() -> new BusinessException("KnowledgeSource not found or access denied"));

        String systemInstructions = promptStrategy.buildPrompt(
                requestDto.numberOfQuestions(),
                Difficulty.valueOf(requestDto.difficulty())
        );

        try {

            GeneratedQuizDto generatedQuiz = chatClient.prompt()
                    .system(systemInstructions)
                    .user(source.getNormalizedContent())
                    .call()
                    .entity(GeneratedQuizDto.class);

            GeneratedQuizValidator.validate(
                    generatedQuiz,
                    requestDto.numberOfQuestions()
            );

            Quiz quiz = buildQuiz(requestDto.title(), owner, source, generatedQuiz);

            Quiz savedQuiz = quizRepository.save(quiz);

            return mapToDto(savedQuiz);

        } catch (Exception e) {
            throw new BusinessException("AI quiz generation failed" + e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GeneratedQuizDto getQuizById(Long quizId, UserDetails userDetails) {

        User owner = getUser(userDetails);

        Quiz quiz = quizRepository
                .findByIdAndOwnerId(quizId, owner.getId())
                .orElseThrow(() -> new BusinessException("Quiz not found or access denied"));

        return mapToDto(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDto> getAllQuizzesByOwner(UserDetails userDetails) {

        User owner = getUser(userDetails);

        List<Quiz> quizzes = quizRepository.findAllByOwnerId(owner.getId());

        return quizzes.stream()
                .map(quiz -> new QuizResponseDto(
                        quiz.getId(),
                        quiz.getOwner().getId(),
                        quiz.getTitle(),
                        quiz.getGeneratedAt(),
                        quiz.getNumberOfSessions()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId, UserDetails userDetails) {

        User owner = getUser(userDetails);

        int deleted = quizRepository.deleteByIdAndOwnerId(quizId, owner.getId());

        if (deleted == 0) {
            throw new BusinessException("Quiz not found or access denied");
        }
}


    //------------------------------------------------------
    // Internal Logic
    //------------------------------------------------------

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));
    }


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