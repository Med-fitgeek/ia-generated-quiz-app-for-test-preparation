package com.fitgeek.IATestPreparator.dtos;

import java.time.LocalDateTime;

public record UpdateUserDto(
        String username,
        String email,
        LocalDateTime updatedAt
) {}
