package com.fitgeek.IATestPreparator.dtos;

import com.fitgeek.IATestPreparator.entities.KnowledgeSource;
import com.fitgeek.IATestPreparator.entities.Quiz;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.entities.enums.SessionStatus;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

public record SessionRequestDto(
        Long quizId,
        SessionStatus status,
        Duration duration
) {}
