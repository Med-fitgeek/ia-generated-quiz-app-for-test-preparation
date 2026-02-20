package com.fitgeek.IATestPreparator.services;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.entities.KnowledgeSource;
import com.fitgeek.IATestPreparator.entities.enums.Difficulty;

public interface QuizGenerationService {

    GeneratedQuizDto generateQuiz(KnowledgeSource source, int numberOfQuestion, Difficulty difficulty);
}
