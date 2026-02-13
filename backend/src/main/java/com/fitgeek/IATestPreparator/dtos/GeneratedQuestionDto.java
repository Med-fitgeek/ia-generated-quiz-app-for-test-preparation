package com.fitgeek.IATestPreparator.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
public record GeneratedQuestionDto (
        String statement,               // Enoncé de la question
        List<String> choices,  // Liste des propositions
        int correctIndex,               // Index de la bonne réponse dans choices
        String explanation,             // Explication détaillée
        String sourceQuote
){}