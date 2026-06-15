package com.fitgeek.memorix.services;

import com.fitgeek.memorix.dtos.KnowledgeNormalizedResponseDto;
import com.fitgeek.memorix.dtos.StrucuturedTextdto;
import com.fitgeek.memorix.entities.KnowledgeSource;
import com.fitgeek.memorix.entities.User;
import com.fitgeek.memorix.entities.enums.SourceType;
import com.fitgeek.memorix.excpetion.BusinessException;
import com.fitgeek.memorix.repositories.KnowledgeSourceRepository;
import com.fitgeek.memorix.services.impl.KnowledgeSourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeSourceServiceImplTest {

    @Mock
    private KnowledgeSourceRepository knowledgeSourceRepository;

    @Mock
    private DocumentProcessingService documentProcessingService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private KnowledgeSourceServiceImpl knowledgeSourceService;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
    }

    // ---------------------------------------------------
    // createFromText
    // ---------------------------------------------------

    @Test
    void createFromText_existingChecksum_returnsExistingSourceWithoutCreating() throws Exception {
        StrucuturedTextdto textDto = new StrucuturedTextdto("Math", "Learn algebra", "Equations", "Notes");
        String checksum = "abc123";

        KnowledgeSource existing = KnowledgeSource.builder().build();
        setId(existing, 10L);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(documentProcessingService.calculateChecksumForText(textDto)).thenReturn(checksum);
        when(knowledgeSourceRepository.findByOwnerIdAndChecksum(owner.getId(), checksum))
                .thenReturn(Optional.of(existing));

        KnowledgeNormalizedResponseDto result = knowledgeSourceService.createFromText(textDto);

        assertEquals(10L, result.sourceId());
        verify(documentProcessingService, never()).normalizeText(any());
        verify(knowledgeSourceRepository, never()).save(any());
    }

    @Test
    void createFromText_newChecksum_createsAndSavesKnowledgeSource() throws Exception {
        StrucuturedTextdto textDto = new StrucuturedTextdto("Math", "Learn algebra", "Equations", "Notes");
        String checksum = "newchecksum";
        String normalized = "normalized content";
        Path path = Path.of("/storage/math.md");

        KnowledgeSource saved = KnowledgeSource.builder().build();
        setId(saved, 99L);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(documentProcessingService.calculateChecksumForText(textDto)).thenReturn(checksum);
        when(knowledgeSourceRepository.findByOwnerIdAndChecksum(owner.getId(), checksum))
                .thenReturn(Optional.empty());
        when(documentProcessingService.normalizeText(textDto)).thenReturn(normalized);
        when(storageService.saveText(owner.getId(), textDto.subject(), normalized)).thenReturn(path);
        when(knowledgeSourceRepository.save(any(KnowledgeSource.class))).thenReturn(saved);

        KnowledgeNormalizedResponseDto result = knowledgeSourceService.createFromText(textDto);

        assertEquals(99L, result.sourceId());

        verify(knowledgeSourceRepository).save(argThat(ks ->
                ks.getOwner().equals(owner)
                        && ks.getSourceType() == SourceType.TEXT
                        && ks.getOriginalFilename().equals("Math")
                        && ks.getNormalizedContent().equals(normalized)
                        && ks.getStoragePath().equals(path.toString())
                        && ks.getChecksum().equals(checksum)
        ));
    }

    @Test
    void createFromText_creationStrategyThrows_wrapsInBusinessException() throws Exception {
        StrucuturedTextdto textDto = new StrucuturedTextdto("Math", "Learn algebra", "Equations", "Notes");
        String checksum = "checksum";

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(documentProcessingService.calculateChecksumForText(textDto)).thenReturn(checksum);
        when(knowledgeSourceRepository.findByOwnerIdAndChecksum(owner.getId(), checksum))
                .thenReturn(Optional.empty());
        when(documentProcessingService.normalizeText(textDto)).thenThrow(new RuntimeException("boom"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> knowledgeSourceService.createFromText(textDto));

        assertEquals("Knowledge source creation failed", ex.getMessage());
    }

    // ---------------------------------------------------
    // createFromDocument
    // ---------------------------------------------------

    @Test
    void createFromDocument_emptyFile_throwsBusinessException() {
        MultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", new byte[0]);

        assertThrows(BusinessException.class,
                () -> knowledgeSourceService.createFromDocument(file));

        verifyNoInteractions(knowledgeSourceRepository, documentProcessingService, storageService);
    }

    @Test
    void createFromDocument_unsupportedExtension_throwsBusinessException() {
        MultipartFile file = new MockMultipartFile("file", "doc.txt", "text/plain", "content".getBytes());

        assertThrows(BusinessException.class,
                () -> knowledgeSourceService.createFromDocument(file));
    }

    @Test
    void createFromDocument_nullFilename_throwsBusinessException() {
        MultipartFile file = new MockMultipartFile("file", null, "application/pdf", "content".getBytes());

        assertThrows(BusinessException.class,
                () -> knowledgeSourceService.createFromDocument(file));
    }

    @Test
    void createFromDocument_existingChecksum_returnsExistingSourceWithoutCreating() throws Exception {
        byte[] bytes = "pdf-bytes".getBytes();
        MultipartFile file = new MockMultipartFile("file", "course.pdf", "application/pdf", bytes);
        String checksum = "filechecksum";

        KnowledgeSource existing = KnowledgeSource.builder().build();
        setId(existing, 5L);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(documentProcessingService.calculateChecksumForFile(bytes)).thenReturn(checksum);
        when(knowledgeSourceRepository.findByOwnerIdAndChecksum(owner.getId(), checksum))
                .thenReturn(Optional.of(existing));

        KnowledgeNormalizedResponseDto result = knowledgeSourceService.createFromDocument(file);

        assertEquals(5L, result.sourceId());
        verify(documentProcessingService, never()).extractAndNormalize(any());
        verify(storageService, never()).saveFile(any(), any(), any());
    }

    @Test
    void createFromDocument_newChecksum_createsAndSavesKnowledgeSource() throws Exception {
        byte[] bytes = "pdf-bytes".getBytes();
        MultipartFile file = new MockMultipartFile("file", "course.pdf", "application/pdf", bytes);
        String checksum = "newfilechecksum";
        String normalized = "normalized doc content";
        Path path = Path.of("/storage/course.pdf");

        KnowledgeSource saved = KnowledgeSource.builder().build();
        setId(saved, 77L);

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(documentProcessingService.calculateChecksumForFile(bytes)).thenReturn(checksum);
        when(knowledgeSourceRepository.findByOwnerIdAndChecksum(owner.getId(), checksum))
                .thenReturn(Optional.empty());
        when(documentProcessingService.extractAndNormalize(bytes)).thenReturn(normalized);
        when(storageService.saveFile(eq(owner.getId()), eq("course.pdf"), any())).thenReturn(path);
        when(knowledgeSourceRepository.save(any(KnowledgeSource.class))).thenReturn(saved);

        KnowledgeNormalizedResponseDto result = knowledgeSourceService.createFromDocument(file);

        assertEquals(77L, result.sourceId());

        verify(knowledgeSourceRepository).save(argThat(ks ->
                ks.getSourceType() == SourceType.DOCUMENT
                        && ks.getOriginalFilename().equals("course.pdf")
                        && ks.getNormalizedContent().equals(normalized)
                        && ks.getStoragePath().equals(path.toString())
                        && ks.getChecksum().equals(checksum)
        ));
    }

    @Test
    void createFromDocument_acceptsDocxAndCsvAndUppercaseExtensions() throws Exception {
        for (String ext : new String[]{"docx", "csv", "PDF"}) {
            byte[] bytes = "bytes".getBytes();
            MultipartFile file = new MockMultipartFile("file", "course." + ext, "application/octet-stream", bytes);

            KnowledgeSource saved = KnowledgeSource.builder().build();
            setId(saved, 1L);

            when(currentUserService.getCurrentUser()).thenReturn(owner);
            when(documentProcessingService.calculateChecksumForFile(bytes)).thenReturn("cs-" + ext);
            when(knowledgeSourceRepository.findByOwnerIdAndChecksum(owner.getId(), "cs-" + ext))
                    .thenReturn(Optional.empty());
            when(documentProcessingService.extractAndNormalize(bytes)).thenReturn("normalized");
            when(storageService.saveFile(any(), any(), any())).thenReturn(Path.of("/storage/x"));
            when(knowledgeSourceRepository.save(any(KnowledgeSource.class))).thenReturn(saved);

            assertDoesNotThrow(() -> knowledgeSourceService.createFromDocument(file));
        }
    }

    private void setId(KnowledgeSource source, Long id) throws Exception {
        var field = KnowledgeSource.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(source, id);
    }
}