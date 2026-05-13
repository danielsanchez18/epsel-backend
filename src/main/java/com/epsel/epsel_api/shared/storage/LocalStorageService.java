package com.epsel.epsel_api.shared.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final String UPLOAD_DIR = "uploads/users/";

    @Override
    public String upload(MultipartFile file) {

        try {

            String fileName =
                    UUID.randomUUID() +
                            "_" +
                            file.getOriginalFilename();

            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();

        } catch (IOException ex) {
            throw new RuntimeException("Error al subir el archivo: " + ex.getMessage());
        }
    }
}
