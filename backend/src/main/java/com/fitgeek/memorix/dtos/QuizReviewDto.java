package com.fitgeek.memorix.dtos;

import java.util.List;


public record QuizReviewDto (double rate, List<QuestionReviewDto> questions){}