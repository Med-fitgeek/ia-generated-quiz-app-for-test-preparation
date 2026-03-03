package com.fitgeek.IATestPreparator.Utils;

import com.fitgeek.IATestPreparator.dtos.StrucuturedTextdto;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.InputStream;
import java.io.IOException;
import java.util.HexFormat;

@Component
public class ChecksumService {

    public String calculateChecksumForDocument(InputStream is) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    public String calculateChecksumForDto(StrucuturedTextdto dto) {
        String rawContent = dto.subject() + "|" +
                dto.objectives() + "|" +
                (dto.keyConcepts() != null ? dto.keyConcepts() : "") + "|" +
                (dto.additionalNotes() != null ? dto.additionalNotes() : "");

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawContent.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while calculating checksum", e);
        }
    }
}