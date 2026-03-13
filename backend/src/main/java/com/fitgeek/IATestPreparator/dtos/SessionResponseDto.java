package com.fitgeek.IATestPreparator.dtos;

import com.fitgeek.IATestPreparator.entities.Quiz;
import com.fitgeek.IATestPreparator.entities.QuizAnswer;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.entities.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SessionResponseDto (
        Long id,
        SessionStatus status,
        int totalQuestions,
        Long rate
) {}
