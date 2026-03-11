package com.fitgeek.IATestPreparator.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuizGenerationRequestDto(
        @NotNull
        Long sourceId,
        @NotNull
        String title,
        @Min(1)
        @Max(20)
        int numberOfQuestions,
        @NotBlank
        String difficulty
) {}
