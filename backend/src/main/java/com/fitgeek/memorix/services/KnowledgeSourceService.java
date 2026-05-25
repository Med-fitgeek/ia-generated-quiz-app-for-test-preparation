package com.fitgeek.memorix.services;

import com.fitgeek.memorix.dtos.KnowledgeNormalizedResponseDto;
import com.fitgeek.memorix.dtos.StrucuturedTextdto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface KnowledgeSourceService {

    KnowledgeNormalizedResponseDto createFromText(StrucuturedTextdto textDto) throws IOException;
    KnowledgeNormalizedResponseDto createFromDocument(MultipartFile file) throws Exception;
}
