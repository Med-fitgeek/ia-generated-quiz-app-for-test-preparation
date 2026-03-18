package com.fitgeek.IATestPreparator.dtos;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt) {}
