package com.fitgeek.IATestPreparator.controllers;

import com.fitgeek.IATestPreparator.dtos.*;
import com.fitgeek.IATestPreparator.services.QuizSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class SessionController {

    private final QuizSessionService quizSessionService;
    @PostMapping
    public ResponseEntity<SessionResponseDto> createSession(@RequestBody  Long quizId) {

        SessionResponseDto sessionResponseDto = quizSessionService.getOrCreateSession(quizId);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionResponseDto);
    }

    @PostMapping("/{sessionId}/submit")
    public ResponseEntity<ResultResponseDto> submitSession(
            @PathVariable Long sessionId,
            @RequestBody SubmitSessionRequestDto request) {

        ResultResponseDto result = quizSessionService.submitSession(sessionId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponseDto> getSessionById(@PathVariable Long sessionId) {

        SessionResponseDto session = quizSessionService.getSessionById(sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping
    public ResponseEntity<Page<SessionResponseDto>> getAllSessions(Pageable pageable) {

        Page<SessionResponseDto> sessions = quizSessionService.getAllSessionsByOwner(pageable);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {

        quizSessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}/result")
    public ResponseEntity<QuizReviewDto> getSessionResult(
            @PathVariable Long sessionId
    ) {

        return ResponseEntity.ok(
                quizSessionService.getSessionResult(sessionId)
        );
    }

    @GetMapping("/{sessionId}/report")
    public ResponseEntity<QuizReportDto> getReport(
            @PathVariable Long sessionId) {

        QuizReportDto report = quizSessionService.getSessionReport(sessionId);

        return ResponseEntity.ok(report);
    }

}
