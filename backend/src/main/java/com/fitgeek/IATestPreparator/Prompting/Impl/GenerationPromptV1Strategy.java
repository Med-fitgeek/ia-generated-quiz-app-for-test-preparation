package com.fitgeek.IATestPreparator.Prompting.Impl;

import com.fitgeek.IATestPreparator.Prompting.GenerationPromptStrategy;
import com.fitgeek.IATestPreparator.entities.enums.Difficulty;
import org.springframework.stereotype.Component;


@Component
public class GenerationPromptV1Strategy implements GenerationPromptStrategy {

    @Override
    public String buildPrompt(int numberOfQuestion, Difficulty difficulty) {
        return """
        Tu es un expert en pédagogie. Ton rôle est de générer un quiz de haute qualité.
        
        INSTRUCTIONS :
        1. Génère exactement %d questions de niveau %s.
        2. Chaque question doit avoir 4 choix, avec un seul choix correct.
        3. Fournis une explication concise pour la réponse.
        4. Pour chaque question, cite obligatoirement le passage précis du texte source (sourceQuote) qui permet de répondre. Si le texte est trop court pour extraire une citation pertinente, reformule l'idée principale du passage concerné.
        
        FORMAT :
        Tu dois répondre uniquement au format JSON.
        Structure :
        {
          "questions": [
            {
              "statement": "La question ici",
              "choices": ["A", "B", "C", "D"],
              "correctIndex": 0,
              "explanation": "Pourquoi c'est A",
              "sourceQuote": "Le passage du texte"
            }
          ]
        }
        """.formatted(numberOfQuestion, difficulty);
    }
}

