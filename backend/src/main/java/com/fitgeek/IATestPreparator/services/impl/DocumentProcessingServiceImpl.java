package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.Utils.ChecksumService;
import com.fitgeek.IATestPreparator.Utils.DocumentExtractor;
import com.fitgeek.IATestPreparator.Utils.KnowledgeNormalizer;
import com.fitgeek.IATestPreparator.dtos.StrucuturedTextdto;
import com.fitgeek.IATestPreparator.services.DocumentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class DocumentProcessingServiceImpl implements DocumentProcessingService {

    private final KnowledgeNormalizer knowledgeNormalizer;
    private final ChecksumService checksumService;
    private final DocumentExtractor documentExtractor;

    @Override
    public String calculateChecksumForText(StrucuturedTextdto dto) throws IOException {
        return checksumService.calculateChecksumForDto(dto);
    }

    @Override
    public String calculateChecksumForFile(byte[] fileBytes) throws IOException, NoSuchAlgorithmException {
        return checksumService.calculateChecksumForDocument(
                new ByteArrayInputStream(fileBytes)
        );
    }

    @Override
    public String normalizeText(StrucuturedTextdto dto) {
        return knowledgeNormalizer.dtoToMarkdown(dto);
    }

    @Override
    public String extractAndNormalize(byte[] fileBytes) throws Exception {

        String extracted = documentExtractor.extractRawText(
                new ByteArrayInputStream(fileBytes)
        );

        return knowledgeNormalizer.rawTextToMarkdown(extracted);
    }
}