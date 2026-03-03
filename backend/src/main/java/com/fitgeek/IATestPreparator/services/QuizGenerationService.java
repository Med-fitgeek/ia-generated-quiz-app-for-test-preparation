package com.fitgeek.IATestPreparator.services;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.dtos.QuizGenerationRequestDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface QuizGenerationService {

    GeneratedQuizDto generateQuiz(QuizGenerationRequestDto requestDto,
                                  UserDetails userDetails);

    GeneratedQuizDto getQuizById(Long quizId, UserDetails userDetails);

    @Transactional(readOnly = true)
    List<GeneratedQuizDto> getAllQuizzesByOwner(UserDetails userDetails);

    @Transactional
    void deleteQuiz(Long quizId, UserDetails userDetails);
}
