package com.fitgeek.memorix.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,
        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,
        @NotBlank(message = "Passeord is required")
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
        String password
){}