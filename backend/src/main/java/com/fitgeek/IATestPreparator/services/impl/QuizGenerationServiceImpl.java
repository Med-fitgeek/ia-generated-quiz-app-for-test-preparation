package com.fitgeek.IATestPreparator.services.impl;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitgeek.IATestPreparator.Prompting.Impl.PromptV1Strategy;
import com.fitgeek.IATestPreparator.Prompting.PromptStrategy;
import com.fitgeek.IATestPreparator.Utils.GeneratedQuizValidator;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuestionDto;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.entities.KnowledgeSource;
import com.fitgeek.IATestPreparator.entities.Question;
import com.fitgeek.IATestPreparator.entities.Quiz;
import com.fitgeek.IATestPreparator.entities.enums.Difficulty;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.repositories.QuizRepository;
import com.fitgeek.IATestPreparator.services.QuizGenerationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizGenerationServiceImpl implements QuizGenerationService {

    private final ChatClient chatClient;
    private final PromptStrategy promptStrategy;
    private final QuizRepository quizRepository;

    public QuizGenerationServiceImpl(ChatClient.Builder chatClientBuilder,
                                     PromptStrategy promptStrategy,
                                     ObjectMapper objectMapper, QuizRepository quizRepository) {
        var validationAdvisor = StructuredOutputValidationAdvisor.builder()
                .objectMapper(objectMapper)
                .outputType(new ParameterizedTypeReference<GeneratedQuizDto>() {})
                .maxRepeatAttempts(3)
                .build();

        this.chatClient = chatClientBuilder.defaultAdvisors(validationAdvisor).build();
        this.promptStrategy = promptStrategy;
        this.quizRepository = quizRepository;

    }

    @Override
    public GeneratedQuizDto generateQuiz(KnowledgeSource source, int numberOfQuestion, Difficulty difficulty) {

        String systemInstructions = promptStrategy.buildPrompt(numberOfQuestion, difficulty);

        try {
            GeneratedQuizDto generatedQuizDto = chatClient.prompt()
                    .system(systemInstructions)
                    .user(source.getNormalizedContent())
                    // .entity() takes care of: formatting, parsing and mapping
                    .call()
                    .entity(GeneratedQuizDto.class);

            GeneratedQuizValidator.validate(generatedQuizDto, numberOfQuestion);

            Quiz quiz = Quiz.builder()
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

}
