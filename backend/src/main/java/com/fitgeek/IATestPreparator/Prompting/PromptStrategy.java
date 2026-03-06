package com.fitgeek.IATestPreparator.Prompting;

import com.fitgeek.IATestPreparator.entities.KnowledgeSource;
import com.fitgeek.IATestPreparator.entities.QuizSession;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.entities.enums.Difficulty;

public interface PromptStrategy {

    String buildPrompt(int numberOfQuestion, Difficulty difficulty);
}
