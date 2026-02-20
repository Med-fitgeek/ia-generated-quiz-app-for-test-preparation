package com.fitgeek.IATestPreparator.dtos;

public record ResultResponseDto (
        int correctCount,
        int size,
        double rate) {
}
