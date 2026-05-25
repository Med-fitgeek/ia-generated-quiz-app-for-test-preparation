package com.fitgeek.memorix.dtos;

import java.time.LocalDateTime;

public record QuizResponseDto(
         Long id,
         Long ownerId,
         String title,
         LocalDateTime generatedAt,
         Long numberOfSessions
) { }
