package com.fitgeek.memorix.dtos;

import com.fitgeek.memorix.entities.enums.SessionStatus;
import lombok.Builder;

@Builder
public record SessionResponseDto (
        Long id,
        SessionStatus status,
        int totalQuestions,
        Long rate
) {}
