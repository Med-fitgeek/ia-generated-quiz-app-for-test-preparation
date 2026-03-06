package com.fitgeek.IATestPreparator.dtos;

public record SubmitSessionWrapperDto(
        Long sessionId,
        SubmitSessionRequestDto sessionRequestDto // ou whatever ton DTO
) {}
