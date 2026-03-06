package com.fitgeek.IATestPreparator.Utils;

import com.fitgeek.IATestPreparator.dtos.StrucuturedTextdto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class KnowledgeNormalizer {

    private final ChatClient chatClient;

    public KnowledgeNormalizer(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String dtoToMarkdown(StrucuturedTextdto dto) {
        StringBuilder sb = new StringBuilder();

        sb.append("# SUBJECT: ").append(dto.subject()).append("\n\n");

        sb.append("## OBJECTIVES:\n").append(dto.objectives()).append("\n\n");

        if (dto.keyConcepts() != null && !dto.keyConcepts().isBlank()) {
            sb.append("## KEY CONCEPTS:\n").append(dto.keyConcepts()).append("\n\n");
        }

        if (dto.additionalNotes() != null && !dto.additionalNotes().isBlank()) {
            sb.append("## ADDITIONAL NOTES:\n").append(dto.additionalNotes()).append("\n");
        }

        return sb.toString();
    }


    public String rawTextToMarkdown(String rawText) {
        return this.chatClient.prompt()
                .system("""
                Tu es un assistant spécialisé dans la structuration de documents pédagogiques.
                Ton rôle est de transformer du texte brut (issu d'un OCR ou d'une extraction PDF) 
                en un document Markdown (.md) parfaitement structuré.
                
                RÈGLES :
                1. Utilise des titres (#, ##, ###) pour la hiérarchie.
                2. Nettoie les erreurs de lecture (mots coupés, caractères spéciaux inutiles).
                3. Conserve TOUTE l'information pédagogique importante.
                4. Utilise des listes à puces pour les énumérations.
                5. NE FAIS AUCUN COMMENTAIRE. Réponds uniquement avec le contenu Markdown.
                """)
                .user("Voici le texte brut à transformer :\n\n" + rawText)
                .call()
                .content();
    }
}
