package com.fitgeek.memorix.dtos;

public record QuizReportDto(
        double rate,
        int correctIndex,
        int totalQuestions,
        String recommendations
) {
}
