package com.fitgeek.IATestPreparator.dtos;

import java.util.List;


public record QuizReviewDto (double rate, List<QuestionReviewDto> questions){}