package com.fitgeek.memorix.dtos;

import java.util.List;

public record GeneratedQuizDto (
        Long quizId,
        String title,
        List<GeneratedQuestionDto> generatedQuestions
) {}
