package com.fitgeek.IATestPreparator.dtos;
import com.fitgeek.IATestPreparator.entities.User;

import java.time.LocalDateTime;

public record QuizResponseDto(
         Long id,
         Long ownerId,
         String title,
         LocalDateTime generatedAt,
         Long numberOfSessions
) { }
