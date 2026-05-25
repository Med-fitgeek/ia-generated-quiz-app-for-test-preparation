package com.fitgeek.memorix.dtos;

import java.util.List;

public record QuestionReviewDto (
        String statement,
        List<String> choices,
        int userAnswer,
        int correctIndex,
        String explanation
){}
