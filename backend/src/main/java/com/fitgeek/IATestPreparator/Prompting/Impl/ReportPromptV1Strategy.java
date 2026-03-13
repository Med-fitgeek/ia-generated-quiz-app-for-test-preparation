package com.fitgeek.IATestPreparator.Prompting.Impl;

import org.springframework.stereotype.Component;

@Component
public class ReportPromptV1Strategy {

    public String buildPrompt(
            int score,
            int correct,
            int total,
            String missedQuestions
    ) {

        return """
        Tu es un expert en pédagogie et en accompagnement d'apprentissage.

        CONTEXTE :
        Un étudiant vient de terminer un quiz.

        Résultat :
        - Score : %d%%
        - Réponses correctes : %d / %d

        Questions mal répondues :
        %s

        OBJECTIF :
        Fournir une recommandation courte et utile pour aider l'étudiant
        à améliorer sa compréhension.

        INSTRUCTIONS :
        - Sois pédagogique
        - Identifie les concepts faibles
        - Propose une stratégie d'amélioration
        - Maximum 120 mots

        FORMAT :
        Retourne uniquement le texte de recommandation.
        """.formatted(score, correct, total, missedQuestions);
    }
}