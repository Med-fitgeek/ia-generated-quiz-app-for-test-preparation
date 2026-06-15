package com.fitgeek.memorix.utils;

import com.fitgeek.memorix.dtos.GeneratedQuestionDto;
import com.fitgeek.memorix.dtos.GeneratedQuizDto;
import com.fitgeek.memorix.excpetion.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratedQuizValidatorTest {

    private GeneratedQuestionDto validQuestion() {
        return new GeneratedQuestionDto(
                "What is 2+2?",
                List.of("3", "4", "5"),
                1,
                "2+2 equals 4",
                "Basic arithmetic states that 2+2=4"
        );
    }

    @Test
    void validate_nullQuizDto_throwsBusinessException() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(null, 1));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertTrue(ex.getMessage().contains("empty"));
    }

    @Test
    void validate_nullGeneratedQuestions_throwsBusinessException() {
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", null);

        assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 1));
    }

    @Test
    void validate_emptyGeneratedQuestions_throwsBusinessException() {
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of());

        assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 1));
    }

    @Test
    void validate_questionCountMismatch_throwsBusinessException() {
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of(validQuestion()));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 2));

        assertTrue(ex.getMessage().contains("Expected 2"));
    }

    @Test
    void validate_blankStatement_throwsBusinessException() {
        GeneratedQuestionDto question = new GeneratedQuestionDto(
                "   ", List.of("A", "B"), 0, "Explanation", "Quote"
        );
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of(question));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 1));

        assertTrue(ex.getMessage().contains("statement"));
    }

    @Test
    void validate_nullChoices_throwsBusinessException() {
        GeneratedQuestionDto question = new GeneratedQuestionDto(
                "Statement", null, 0, "Explanation", "Quote"
        );
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of(question));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 1));

        assertTrue(ex.getMessage().contains("2 choices"));
    }

    @Test
    void validate_lessThanTwoChoices_throwsBusinessException() {
        GeneratedQuestionDto question = new GeneratedQuestionDto(
                "Statement", List.of("OnlyOne"), 0, "Explanation", "Quote"
        );
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of(question));

        assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 1));
    }

    @Test
    void validate_negativeCorrectIndex_throwsBusinessException() {
        GeneratedQuestionDto question = new GeneratedQuestionDto(
                "Statement", List.of("A", "B"), -1, "Explanation", "Quote"
        );
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of(question));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 1));

        assertTrue(ex.getMessage().contains("out of bounds"));
    }

    @Test
    void validate_correctIndexTooLarge_throwsBusinessException() {
        GeneratedQuestionDto question = new GeneratedQuestionDto(
                "Statement", List.of("A", "B"), 2, "Explanation", "Quote"
        );
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of(question));

        assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 1));
    }

    @Test
    void validate_blankExplanation_throwsBusinessException() {
        GeneratedQuestionDto question = new GeneratedQuestionDto(
                "Statement", List.of("A", "B"), 0, "", "Quote"
        );
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of(question));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 1));

        assertTrue(ex.getMessage().contains("Explanation"));
    }

    @Test
    void validate_blankSourceQuote_throwsBusinessException() {
        GeneratedQuestionDto question = new GeneratedQuestionDto(
                "Statement", List.of("A", "B"), 0, "Explanation", "   "
        );
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of(question));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> GeneratedQuizValidator.validate(quizDto, 1));

        assertTrue(ex.getMessage().contains("Source quote"));
    }

    @Test
    void validate_validQuiz_doesNotThrow() {
        GeneratedQuizDto quizDto = new GeneratedQuizDto(1L, "Quiz Title", List.of(validQuestion(), validQuestion()));

        assertDoesNotThrow(() -> GeneratedQuizValidator.validate(quizDto, 2));
    }
}