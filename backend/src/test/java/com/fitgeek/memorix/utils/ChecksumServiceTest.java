package com.fitgeek.memorix.utils;

import com.fitgeek.memorix.dtos.StrucuturedTextdto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class ChecksumServiceTest {

    private ChecksumService checksumService;

    @BeforeEach
    void setUp() {
        checksumService = new ChecksumService();
    }

    @Test
    void calculateChecksumForDocument_returnsCorrectSha256Hex() throws Exception {
        byte[] content = "Hello, Memorix!".getBytes(StandardCharsets.UTF_8);
        InputStream is = new ByteArrayInputStream(content);

        String result = checksumService.calculateChecksumForDocument(is);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String expected = HexFormat.of().formatHex(digest.digest(content));

        assertEquals(expected, result);
        assertEquals(64, result.length());
    }

    @Test
    void calculateChecksumForDocument_emptyStream_returnsHashOfEmptyContent() throws Exception {
        InputStream is = new ByteArrayInputStream(new byte[0]);

        String result = checksumService.calculateChecksumForDocument(is);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String expected = HexFormat.of().formatHex(digest.digest(new byte[0]));

        assertEquals(expected, result);
    }

    @Test
    void calculateChecksumForDocument_largeContent_readsInChunks() throws Exception {
        byte[] content = new byte[20000];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        InputStream is = new ByteArrayInputStream(content);

        String result = checksumService.calculateChecksumForDocument(is);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String expected = HexFormat.of().formatHex(digest.digest(content));

        assertEquals(expected, result);
    }

    @Test
    void calculateChecksumForDto_returnsConsistentChecksumForSameInput() {
        StrucuturedTextdto dto = new StrucuturedTextdto(
                "Math", "Learn algebra", "Equations", "Be careful with signs"
        );

        String result1 = checksumService.calculateChecksumForDto(dto);
        String result2 = checksumService.calculateChecksumForDto(dto);

        assertEquals(result1, result2);
        assertEquals(64, result1.length());
    }

    @Test
    void calculateChecksumForDto_differentInputs_produceDifferentChecksums() {
        StrucuturedTextdto dto1 = new StrucuturedTextdto("Math", "Learn algebra", "Equations", "Notes");
        StrucuturedTextdto dto2 = new StrucuturedTextdto("Physics", "Learn algebra", "Equations", "Notes");

        String result1 = checksumService.calculateChecksumForDto(dto1);
        String result2 = checksumService.calculateChecksumForDto(dto2);

        assertNotEquals(result1, result2);
    }

    @Test
    void calculateChecksumForDto_handlesNullOptionalFields() {
        StrucuturedTextdto dto = new StrucuturedTextdto("Math", "Learn algebra", null, null);

        String result = checksumService.calculateChecksumForDto(dto);

        assertNotNull(result);
        assertEquals(64, result.length());
    }

}