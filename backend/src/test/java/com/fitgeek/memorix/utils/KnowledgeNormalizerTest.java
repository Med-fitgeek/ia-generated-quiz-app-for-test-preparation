package com.fitgeek.memorix.utils;

import com.fitgeek.memorix.dtos.StrucuturedTextdto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeNormalizerTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    private KnowledgeNormalizer knowledgeNormalizer;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        knowledgeNormalizer = new KnowledgeNormalizer(chatClientBuilder);
    }

    @Test
    void dtoToMarkdown_withAllFields_buildsExpectedMarkdown() {
        StrucuturedTextdto dto = new StrucuturedTextdto(
                "History",
                "Treaty of Versailles, Totalitarianism",
                "Understand WWII causes",
                "Focus on European front"
        );

        String result = knowledgeNormalizer.dtoToMarkdown(dto);

        assertTrue(result.contains("# SUBJECT: History"));
        assertTrue(result.contains("## OBJECTIVES:\nUnderstand WWII causes"));
        assertTrue(result.contains("## KEY CONCEPTS:\nTreaty of Versailles, Totalitarianism"));
        assertTrue(result.contains("## ADDITIONAL NOTES:\nFocus on European front"));
    }

    @Test
    void dtoToMarkdown_withoutOptionalFields_omitsOptionalSections() {
        StrucuturedTextdto dto = new StrucuturedTextdto(
                "History",
                null,
                "Understand WWII causes",
                ""
        );

        String result = knowledgeNormalizer.dtoToMarkdown(dto);

        assertTrue(result.contains("# SUBJECT: History"));
        assertTrue(result.contains("## OBJECTIVES:\nUnderstand WWII causes"));
        assertFalse(result.contains("## KEY CONCEPTS:"));
        assertFalse(result.contains("## ADDITIONAL NOTES:"));
    }

    @Test
    void dtoToMarkdown_withBlankKeyConcepts_omitsKeyConceptsSection() {
        StrucuturedTextdto dto = new StrucuturedTextdto(
                "History", "   ", "Understand WWII causes", null
        );

        String result = knowledgeNormalizer.dtoToMarkdown(dto);

        assertFalse(result.contains("## KEY CONCEPTS:"));
    }

    @Test
    void rawTextToMarkdown_returnsContentFromChatClient() {
        String rawText = "raw ocr text";
        String expectedMarkdown = "# Structured\n\nContent here";

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(expectedMarkdown);

        String result = knowledgeNormalizer.rawTextToMarkdown(rawText);

        assertEquals(expectedMarkdown, result);
        verify(requestSpec).user("Voici le texte brut à transformer :\n\n" + rawText);
    }
}