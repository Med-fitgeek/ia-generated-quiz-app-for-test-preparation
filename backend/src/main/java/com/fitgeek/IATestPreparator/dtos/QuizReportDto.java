package com.fitgeek.IATestPreparator.dtos;

public record QuizReportDto(
        double rate,
        int correctAnswers,
        int totalQuestions,
        String recommendations
) {
}
