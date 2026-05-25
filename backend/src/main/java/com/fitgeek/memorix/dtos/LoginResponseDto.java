package com.fitgeek.memorix.dtos;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
