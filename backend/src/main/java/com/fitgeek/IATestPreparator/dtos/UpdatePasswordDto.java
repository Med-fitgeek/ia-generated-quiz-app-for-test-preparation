package com.fitgeek.IATestPreparator.dtos;

public record UpdatePasswordDto(
        String currentPassword,
        String newPassword
) {}