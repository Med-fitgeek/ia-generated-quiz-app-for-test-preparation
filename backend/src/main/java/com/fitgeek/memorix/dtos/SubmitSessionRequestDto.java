package com.fitgeek.memorix.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitSessionRequestDto(
        @NotNull
        @NotEmpty
        List<Integer> answers
) {}

