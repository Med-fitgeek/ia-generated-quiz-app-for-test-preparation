package com.fitgeek.IATestPreparator.dtos;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        String email,
        String avatarId,
        LocalDateTime createdAt) {}
