package com.fitgeek.IATestPreparator.dtos;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
