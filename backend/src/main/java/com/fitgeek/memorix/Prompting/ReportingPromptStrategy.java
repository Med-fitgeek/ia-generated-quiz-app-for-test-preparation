package com.fitgeek.memorix.Prompting;

public interface ReportingPromptStrategy {
    String buildPrompt(
            int score,
            int correct,
            int total,
            String missedQuestions
    );
}
