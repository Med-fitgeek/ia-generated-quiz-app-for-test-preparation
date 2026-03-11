package com.fitgeek.IATestPreparator.Utils;

import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuestionDto;
import org.springframework.http.HttpStatus;

public class GeneratedQuizValidator {

    public static void validate(GeneratedQuizDto quizDto, int expectedCount) {

        if (quizDto == null || quizDto.generatedQuestions() == null || quizDto.generatedQuestions().isEmpty())
            throw new BusinessException("Generated quiz is empty", HttpStatus.INTERNAL_SERVER_ERROR);

        if (quizDto.generatedQuestions().size() != expectedCount)
            throw new BusinessException("Expected " + expectedCount + " questions but got " + quizDto.generatedQuestions().size(), HttpStatus.INTERNAL_SERVER_ERROR);

        for (GeneratedQuestionDto question : quizDto.generatedQuestions()) {

            if (question.statement() == null || question.statement().isBlank())
                throw new BusinessException("Question statement is empty", HttpStatus.INTERNAL_SERVER_ERROR);

            if (question.choices() ==  null || question.choices().size() < 2)
                throw new BusinessException("Question must have at least 2 choices", HttpStatus.INTERNAL_SERVER_ERROR);

            if (question.correctIndex() < 0 || question.correctIndex() > question.choices().size() -1)
                throw new BusinessException("Correct index out of bounds", HttpStatus.INTERNAL_SERVER_ERROR);

            if (question.explanation() == null || question.explanation().isBlank())
                throw new BusinessException("Explanation is missing", HttpStatus.INTERNAL_SERVER_ERROR);

            if (question.sourceQuote() == null || question.sourceQuote().isBlank())
                throw new BusinessException("Source quote is missing", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
