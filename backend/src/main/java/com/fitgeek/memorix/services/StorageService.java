package com.fitgeek.memorix.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {

    Path saveText(Long ownerId, String filename, String content) throws IOException;
    Path saveFile(Long ownerId, String filename, MultipartFile file) throws IOException;
}
