package com.fitgeek.IATestPreparator.Prompting;

public interface ReportingPromptStrategy {
    String buildPrompt(
            int score,
            int correct,
            int total,
            String missedQuestions
    );
}
