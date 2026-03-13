package com.fitgeek.IATestPreparator.Prompting;

import com.fitgeek.IATestPreparator.entities.enums.Difficulty;

public interface GenerationPromptStrategy {

    String buildPrompt(int numberOfQuestion, Difficulty difficulty);
}
