package com.fitgeek.memorix.dtos;

public record UpdatePasswordDto(
        String currentPassword,
        String newPassword
) {}