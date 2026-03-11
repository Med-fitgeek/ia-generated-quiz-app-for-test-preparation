package com.fitgeek.IATestPreparator.services;

import com.fitgeek.IATestPreparator.dtos.KnowledgeNormalizedResponseDto;
import com.fitgeek.IATestPreparator.dtos.StrucuturedTextdto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface KnowledgeSourceService {

    KnowledgeNormalizedResponseDto createFromText(StrucuturedTextdto textDto) throws IOException;
    KnowledgeNormalizedResponseDto createFromDocument(MultipartFile file) throws Exception;
}
