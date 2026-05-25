package com.fitgeek.memorix.services;
import com.fitgeek.memorix.dtos.GeneratedQuizDto;
import com.fitgeek.memorix.dtos.QuizGenerationRequestDto;
import com.fitgeek.memorix.dtos.QuizResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface QuizGenerationService {

    GeneratedQuizDto generateQuiz(QuizGenerationRequestDto requestDto);

    GeneratedQuizDto getQuizById(Long quizId);

    @Transactional(readOnly = true)
    Page<QuizResponseDto> getAllQuizzesByOwner(Pageable pageable);

    @Transactional
    void deleteQuiz(Long quizId);
}
