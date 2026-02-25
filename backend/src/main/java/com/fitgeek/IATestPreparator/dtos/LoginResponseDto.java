package com.fitgeek.IATestPreparator.dtos;

public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {}
