package com.fitgeek.IATestPreparator.controllers;

import com.fitgeek.IATestPreparator.dtos.KnowledgeNormalizedResponseDto;
import com.fitgeek.IATestPreparator.dtos.KnowledgeRequestDto;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.services.KnowledgeSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/source")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class KnowledgeSourceController {

    private final KnowledgeSourceService knowledgeSourceService;

    @PostMapping(value ="/upload", consumes = "multipart/form-data")
    public ResponseEntity<KnowledgeNormalizedResponseDto> uploadSource(
            @ModelAttribute KnowledgeRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        boolean hasDocument = requestDto.file() != null && !requestDto.file().isEmpty();
        boolean hasText = requestDto.strucuturedTextDto() != null;

        if (hasDocument == hasText)
            throw new BusinessException("You must provide either a document or structured text");

        KnowledgeNormalizedResponseDto response = hasDocument
                ? knowledgeSourceService.createFromDocument(requestDto.file(), userDetails)
                : knowledgeSourceService.createFromText(requestDto.strucuturedTextDto(), userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
