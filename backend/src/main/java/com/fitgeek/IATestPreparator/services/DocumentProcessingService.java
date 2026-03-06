package com.fitgeek.IATestPreparator.services;

import com.fitgeek.IATestPreparator.dtos.StrucuturedTextdto;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface DocumentProcessingService {

    String calculateChecksumForText(StrucuturedTextdto dto) throws IOException;

    String calculateChecksumForFile(byte[] fileBytes) throws IOException, NoSuchAlgorithmException;

    String normalizeText(StrucuturedTextdto dto);

    String extractAndNormalize(byte[] fileBytes) throws Exception;
}