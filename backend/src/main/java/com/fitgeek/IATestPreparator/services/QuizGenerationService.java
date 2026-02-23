package com.fitgeek.IATestPreparator.services;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.dtos.QuizGenerationRequestDto;
import com.fitgeek.IATestPreparator.entities.KnowledgeSource;
import com.fitgeek.IATestPreparator.entities.enums.Difficulty;
import org.springframework.security.core.userdetails.UserDetails;

public interface QuizGenerationService {

    GeneratedQuizDto generateQuiz(QuizGenerationRequestDto requestDto,
                                  UserDetails userDetails);

    GeneratedQuizDto getQuizById(Long id, UserDetails userDetails);
}
