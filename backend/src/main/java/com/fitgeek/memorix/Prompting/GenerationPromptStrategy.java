package com.fitgeek.memorix.Prompting;

import com.fitgeek.memorix.entities.enums.Difficulty;

public interface GenerationPromptStrategy {

    String buildPrompt(int numberOfQuestion, Difficulty difficulty);
}
