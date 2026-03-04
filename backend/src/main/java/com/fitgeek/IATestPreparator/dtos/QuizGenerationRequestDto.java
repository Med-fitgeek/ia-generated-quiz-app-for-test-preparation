package com.fitgeek.IATestPreparator.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuizGenerationRequestDto(
        @NotNull
        Long sourceId,
        @NotNull
        String title,
        @NotNull
        int numberOfQuestions,
        @NotBlank
        String difficulty
) {}
