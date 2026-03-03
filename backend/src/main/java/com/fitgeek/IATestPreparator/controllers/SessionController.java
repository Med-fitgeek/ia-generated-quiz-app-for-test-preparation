package com.fitgeek.IATestPreparator.controllers;

import com.fitgeek.IATestPreparator.dtos.*;
import com.fitgeek.IATestPreparator.services.QuizSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class SessionController {

    private final QuizSessionService quizSessionService;
    @PostMapping("/create")
    public ResponseEntity<SessionResponseDto> createSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody  Long quizId
    ) {
        SessionResponseDto sessionResponseDto =
                quizSessionService.getOrCreateSession(userDetails, quizId);

        return ResponseEntity.status(HttpStatus.CREATED).body(sessionResponseDto);
    }

    @PostMapping("/{sessionId}/submit")
    public ResponseEntity<ResultResponseDto> submitSession(
            @PathVariable Long sessionId,
            @RequestBody SubmitSessionRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ResultResponseDto result =
                quizSessionService.submitSession(userDetails, sessionId, request);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponseDto> getSessionById(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SessionResponseDto session =
                quizSessionService.getSessionById(sessionId, userDetails);

        return ResponseEntity.ok(session);
    }

    @GetMapping
    public ResponseEntity<List<SessionResponseDto>> getAllSessions(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<SessionResponseDto> sessions =
                quizSessionService.getAllSessionsByOwner(userDetails);

        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        quizSessionService.deleteSession(sessionId, userDetails);

        return ResponseEntity.noContent().build();
    }

}
