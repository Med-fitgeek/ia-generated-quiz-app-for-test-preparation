package com.fitgeek.memorix.services.impl;

import com.fitgeek.memorix.Prompting.ReportingPromptStrategy;
import com.fitgeek.memorix.entities.Question;
import com.fitgeek.memorix.entities.QuizReport;
import com.fitgeek.memorix.entities.QuizSession;
import com.fitgeek.memorix.services.QuizReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizReportServiceImpl implements QuizReportService {

    private final ChatClient chatClient;
    private final ReportingPromptStrategy promptStrategy;

    @Override
    public QuizReport generateReport(QuizSession session) {

        String missedContext = buildMissedQuestionsContext(session);

        int score = session.getRate() != null
                ? session.getRate().intValue()
                : 0;

        String prompt = promptStrategy.buildPrompt(
                score,
                session.getCorrectCount(),
                session.getTotalQuestions(),
                missedContext
        );

        String recommendation = chatClient.prompt()
                .system(prompt)
                .call()
                .content();

        return QuizReport.builder()
                .session(session)
                .rate(score)
                .correctAnswers(session.getCorrectCount())
                .totalQuestions(session.getTotalQuestions())
                .recommendations(recommendation)
                .build();
    }

    private String buildMissedQuestionsContext(QuizSession session) {

        return session.getAnswers()
                .stream()
                .filter(a -> !a.isCorrect())
                .map(a -> {

                    Question q = a.getQuestion();

                    return """
                    Question: %s
                    Correct explanation: %s
                    Source concept: %s
                    """.formatted(
                            q.getStatement(),
                            q.getExplanation(),
                            q.getSourceQuote()
                    );

                })
                .collect(Collectors.joining("\n"));
    }
}