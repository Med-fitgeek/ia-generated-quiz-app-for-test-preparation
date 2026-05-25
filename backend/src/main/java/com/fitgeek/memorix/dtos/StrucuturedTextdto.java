package com.fitgeek.memorix.dtos;

import jakarta.validation.constraints.NotBlank;

public record StrucuturedTextdto (
        @NotBlank(message = "Subject is required")
        String subject,
        String keyConcepts,
        @NotBlank(message = "Your objectives are required")
        String objectives,
        String additionalNotes

) { }
