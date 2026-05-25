package com.fitgeek.memorix.dtos;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        String email,
        String avatarId,
        LocalDateTime createdAt) {}
