package com.fitgeek.IATestPreparator.dtos;

public record QuizReportDto(
        double rate,
        int correctIndex,
        int totalQuestions,
        String recommendations
) {
}
