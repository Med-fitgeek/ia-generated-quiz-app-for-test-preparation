package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.dtos.KnowledgeNormalizedResponseDto;
import com.fitgeek.IATestPreparator.dtos.StrucuturedTextdto;
import com.fitgeek.IATestPreparator.entities.KnowledgeSource;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.entities.enums.SourceType;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.repositories.KnowledgeSourceRepository;
import com.fitgeek.IATestPreparator.services.CurrentUserService;
import com.fitgeek.IATestPreparator.services.DocumentProcessingService;
import com.fitgeek.IATestPreparator.services.KnowledgeSourceService;
import com.fitgeek.IATestPreparator.services.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class KnowledgeSourceServiceImpl implements KnowledgeSourceService {

    private final KnowledgeSourceRepository knowledgeSourceRepository;
    private final DocumentProcessingService documentProcessingService;
    private final CurrentUserService currentUserService;
    private final StorageService storageService;

    @Override
    @Transactional
    public KnowledgeNormalizedResponseDto createFromText(StrucuturedTextdto textDto) throws IOException {

        User owner = currentUserService.getCurrentUser();
        String checksum = documentProcessingService.calculateChecksumForText(textDto);

        return findExistingOrCreate(
                owner,
                checksum,
                textDto.subject(),
                SourceType.TEXT,
                () -> {
                    String normalized = documentProcessingService.normalizeText(textDto);
                    Path path = storageService.saveText(owner.getId(), textDto.subject(), normalized);
                    return new CreationPayload(normalized, path);
                }
        );
    }

    @Override
    @Transactional
    public KnowledgeNormalizedResponseDto createFromDocument(MultipartFile file
    ) throws IOException, NoSuchAlgorithmException {

        if (file.isEmpty()) {
            throw new BusinessException("File is empty", HttpStatus.BAD_REQUEST);
        }

        validateExtension(file.getOriginalFilename());

        User owner = currentUserService.getCurrentUser();

        byte[] bytes = file.getBytes();

        String checksum = documentProcessingService.calculateChecksumForFile(bytes);

        return findExistingOrCreate(
                owner,
                checksum,
                file.getOriginalFilename(),
                SourceType.DOCUMENT,
                () -> {
                    String normalized = documentProcessingService.extractAndNormalize(bytes);
                    Path path = storageService.saveFile(owner.getId(), file.getOriginalFilename(), file);
                    return new CreationPayload(normalized, path);
                }
        );
    }

    private KnowledgeNormalizedResponseDto findExistingOrCreate(
            User owner,
            String checksum,
            String filename,
            SourceType type,
            CreationStrategy strategy
    )  {

        return knowledgeSourceRepository
                .findByOwnerIdAndChecksum(owner.getId(), checksum)
                .map(existing -> new KnowledgeNormalizedResponseDto(existing.getId()))
                .orElseGet(() -> {

                    try {
                        CreationPayload payload = strategy.create();

                        KnowledgeSource entity = KnowledgeSource.builder()
                                .owner(owner)
                                .sourceType(type)
                                .originalFilename(filename)
                                .normalizedContent(payload.normalizedContent())
                                .storagePath(payload.path().toString())
                                .checksum(checksum)
                                .build();

                        KnowledgeSource saved = knowledgeSourceRepository.save(entity);

                        return new KnowledgeNormalizedResponseDto(saved.getId());

                    } catch (Exception e) {
                        throw new BusinessException("Creation failed : ", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
    }

    private void validateExtension(String filename) {
        if (filename == null ||
                !(filename.toLowerCase().endsWith(".pdf")
                        || filename.toLowerCase().endsWith(".docx")
                        || filename.toLowerCase().endsWith(".csv"))) {
            throw new BusinessException("File type not supported", HttpStatus.BAD_REQUEST);
        }
    }

    private record CreationPayload(String normalizedContent, Path path) {}
    private interface CreationStrategy {
        CreationPayload create() throws Exception;
    }
}