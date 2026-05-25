package com.fitgeek.memorix.dtos;

public record SubmitSessionWrapperDto(
        Long sessionId,
        SubmitSessionRequestDto sessionRequestDto // ou whatever ton DTO
) {}
