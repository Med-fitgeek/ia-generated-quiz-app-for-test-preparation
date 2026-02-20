package com.fitgeek.IATestPreparator.controllers;

import com.fitgeek.IATestPreparator.dtos.*;
import com.fitgeek.IATestPreparator.services.QuizSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class SessionController {

    private final QuizSessionService quizSessionService;
    @PostMapping("/create")
    public ResponseEntity<SessionResponseDto> createSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody final SessionRequestDto sessionRequestDto
    ) {
        SessionResponseDto sessionResponseDto = quizSessionService.createSession(userDetails, sessionRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(sessionResponseDto);
    }

    @PostMapping("/start")
    public ResponseEntity<SessionResponseDto> startSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody  Long sessionId
    ) {
        SessionResponseDto sessionResponseDto = quizSessionService.startSession(userDetails, sessionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionResponseDto);
    }

    @PostMapping("/submit")
    public ResponseEntity<ResultResponseDto> submitSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SubmitSessionWrapperDto sessionWrapperDto
            ) {

        Long sessionId = sessionWrapperDto.sessionId();
        SubmitSessionRequestDto SubmitSessionRequestDto = sessionWrapperDto.sessionRequestDto();

        ResultResponseDto result = quizSessionService.submitSession(userDetails, sessionId, SubmitSessionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


}
