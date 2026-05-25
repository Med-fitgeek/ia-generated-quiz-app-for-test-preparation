package com.fitgeek.memorix.dtos;

import com.fitgeek.memorix.entities.enums.SessionStatus;

import java.time.Duration;

public record SessionRequestDto(
        Long quizId,
        SessionStatus status,
        Duration duration
) {}
