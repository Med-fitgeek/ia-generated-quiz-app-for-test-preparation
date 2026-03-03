package com.fitgeek.IATestPreparator.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitgeek.IATestPreparator.Prompting.Impl.PromptV1Strategy;
import com.fitgeek.IATestPreparator.Prompting.PromptStrategy;
import com.fitgeek.IATestPreparator.Utils.GeneratedQuizValidator;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuestionDto;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.dtos.QuizGenerationRequestDto;
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
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QuizGenerationServiceImpl implements QuizGenerationService {

    private final ChatClient chatClient;
    private final PromptStrategy promptStrategy;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final KnowledgeSourceRepository sourceRepository;


    public QuizGenerationServiceImpl(ChatClient.Builder chatClientBuilder,
                                     PromptStrategy promptStrategy,
                                     ObjectMapper objectMapper,
                                     QuizRepository quizRepository,
                                     UserRepository userRepository,
                                     KnowledgeSourceRepository sourceRepository) {
        var validationAdvisor = StructuredOutputValidationAdvisor.builder()
                .objectMapper(objectMapper)
                .outputType(new ParameterizedTypeReference<GeneratedQuizDto>() {})
                .maxRepeatAttempts(3)
                .build();

        this.chatClient = chatClientBuilder.defaultAdvisors(validationAdvisor).build();
        this.promptStrategy = promptStrategy;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.sourceRepository = sourceRepository;
    }

    @Override
    public GeneratedQuizDto generateQuiz(QuizGenerationRequestDto requestDto,
                                         UserDetails userDetails) {

        User owner = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        KnowledgeSource source = sourceRepository
                .findById(requestDto.sourceId())
                .orElseThrow(() -> new BusinessException("KnowledgeSource not found"));

        if (!source.getOwner().getId().equals(owner.getId())) {
            throw new BusinessException("You do not own this knowledge source");
        }

        String systemInstructions = promptStrategy.buildPrompt(requestDto.numberOfQuestions(), Difficulty.valueOf(requestDto.difficulty()));

        try {
            GeneratedQuizDto generatedQuizDto = chatClient.prompt()
                    .system(systemInstructions)
                    .user(source.getNormalizedContent())
                    // .entity() takes care of: formatting, parsing and mapping
                    .call()
                    .entity(GeneratedQuizDto.class);

            GeneratedQuizValidator.validate(generatedQuizDto, requestDto.numberOfQuestions());

            Quiz quiz = Quiz.builder()
                    .owner(owner)
                    .sourceChecksum(source.getChecksum())
                    .generatorVersion(PromptV1Strategy.class.getName())
                    .build();

            generatedQuizDto.generatedQuestions().forEach(dto -> {
                Question question = Question.builder()
                        .statement(dto.statement())
                        .choices(dto.choices())
                        .correctIndex(dto.correctIndex())
                        .explanation(dto.explanation())
                        .sourceQuote(dto.sourceQuote())
                        .build();

                quiz.addQuestion(question);
            });

            Quiz savedQuiz = quizRepository.save(quiz);


            return new GeneratedQuizDto(
                    savedQuiz.getId(),
                    generatedQuizDto.generatedQuestions()
            );

        } catch (Exception e) {
            // Spring AI lèvera une exception si le retry échoue ou si le format est invalide
            throw new BusinessException("IA could not generate a valide quiz : " + e.getMessage());
        }
    }

    @Override
    public GeneratedQuizDto getQuizById(Long id, UserDetails userDetails) {

        User owner = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        try {

            Quiz savedQuiz = quizRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Quiz not found"));

            if (!savedQuiz.getOwner().getId().equals(owner.getId())) {
                throw new BusinessException("Access denied");
            }

            List<GeneratedQuestionDto> questionDtos = savedQuiz.getQuestions()
                    .stream()
                    .map(question -> GeneratedQuestionDto.builder()
                            .statement(question.getStatement())
                            .choices(question.getChoices())
                            .correctIndex(question.getCorrectIndex())
                            .explanation(question.getExplanation())
                            .sourceQuote(question.getSourceQuote())
                            .build())
                    .toList();

            return new GeneratedQuizDto(savedQuiz.getId(), questionDtos);

        } catch (Exception e) {
            throw new BusinessException("IA could not get a quiz : " + e.getMessage());
        }



    }

}
