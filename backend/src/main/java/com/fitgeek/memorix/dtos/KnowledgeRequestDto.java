package com.fitgeek.memorix.dtos;

import org.springframework.web.multipart.MultipartFile;

public record KnowledgeRequestDto(
        MultipartFile file,
        StrucuturedTextdto strucuturedTextDto
) {}
