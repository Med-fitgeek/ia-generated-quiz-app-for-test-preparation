package com.fitgeek.memorix.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record GeneratedQuestionDto (
        String statement,
        List<String> choices,
        int correctIndex,
        String explanation,
        String sourceQuote
){}