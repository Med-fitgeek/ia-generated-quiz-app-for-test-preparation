package com.fitgeek.memorix.dtos;

import java.time.LocalDateTime;

public record UpdateUserDto(
        String username,
        String email,
        LocalDateTime updatedAt,
        String avatarId
) {}
