package com.fitgeek.memorix.services.impl;


import com.fitgeek.memorix.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class StorageServiceImpl implements StorageService {

    @Value("${upload.path}")
    private String uploadPath;

    public Path saveText(Long ownerId, String filename, String content) throws IOException {

        Path path = Paths.get(uploadPath, ownerId.toString(), filename);

        Files.createDirectories(path.getParent());

        Files.writeString(
                path,
                content,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        return path;
    }

    public Path saveFile(Long ownerId, String filename, MultipartFile file) throws IOException {

        Path path = Paths.get(uploadPath, ownerId.toString(), filename);
        Files.createDirectories(path.getParent());
        file.transferTo(new java.io.File(String.valueOf(path)));

        return path;
    }
}
