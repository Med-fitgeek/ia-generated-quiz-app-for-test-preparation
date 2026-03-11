package com.fitgeek.IATestPreparator.services;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.dtos.QuizGenerationRequestDto;
import com.fitgeek.IATestPreparator.dtos.QuizResponseDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface QuizGenerationService {

    GeneratedQuizDto generateQuiz(QuizGenerationRequestDto requestDto);

    GeneratedQuizDto getQuizById(Long quizId);

    @Transactional(readOnly = true)
    List<QuizResponseDto> getAllQuizzesByOwner();

    @Transactional
    void deleteQuiz(Long quizId);
}
