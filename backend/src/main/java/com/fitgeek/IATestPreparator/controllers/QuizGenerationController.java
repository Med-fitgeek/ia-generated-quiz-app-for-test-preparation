package com.fitgeek.IATestPreparator.controllers;

import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.dtos.QuizGenerationRequestDto;
import com.fitgeek.IATestPreparator.dtos.QuizResponseDto;
import com.fitgeek.IATestPreparator.services.QuizGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class QuizGenerationController {
    private final QuizGenerationService quizGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<GeneratedQuizDto> generateQuiz(@Valid @RequestBody QuizGenerationRequestDto requestDto) {

        GeneratedQuizDto quizDto = quizGenerationService.generateQuiz(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizDto);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<GeneratedQuizDto> getQuizById(@PathVariable Long quizId) {

        GeneratedQuizDto quizDto = quizGenerationService.getQuizById(quizId);
        return ResponseEntity.status(HttpStatus.OK).body(quizDto);
    }

    @GetMapping("")
    public ResponseEntity<Page<QuizResponseDto>> getAllQuizzes(
            @PageableDefault(size = 10, sort = "generatedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<QuizResponseDto> quizzes =
                quizGenerationService.getAllQuizzesByOwner(pageable);
        return ResponseEntity.ok(quizzes);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<HttpStatus> deleteQuiz(@PathVariable Long quizId) {

        quizGenerationService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}
