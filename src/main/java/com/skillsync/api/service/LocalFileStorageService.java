package com.skillsync.api.service;

import com.skillsync.api.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Profile("!aws")
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.storage.location:uploads}")
    private String storageLocation;
    
    @Value("${file.storage.base-url:http://localhost:8081/api/v1/files}")
    private String baseUrl;

    private Path getStoragePath() {
        return Paths.get(storageLocation).toAbsolutePath().normalize();
    }

    @Override
    public String storeFile(MultipartFile file, String folderPath, String fileName) throws IOException {
        try {
            // Generate a unique file name if not provided
            if (fileName == null || fileName.isEmpty()) {
                fileName = UUID.randomUUID().toString();
            }
            
            // Add file extension if there is one
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                fileName = fileName + extension;
            }
            
            // Normalize and create the folder path
            String normalizedFolderPath = folderPath;
            if (normalizedFolderPath.startsWith("/")) {
                normalizedFolderPath = normalizedFolderPath.substring(1);
            }
            
            Path targetFolder = getStoragePath().resolve(normalizedFolderPath);
            Files.createDirectories(targetFolder);
            
            // Save the file
            Path targetLocation = targetFolder.resolve(fileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            
            log.info("Successfully stored file locally: {}", targetLocation);
            
            // Create a URL for accessing the file
            String finalPath = normalizedFolderPath + "/" + fileName;
            return baseUrl + "/" + finalPath;
        } catch (IOException ex) {
            log.error("Error storing file locally", ex);
            throw new FileStorageException("Failed to store file: " + ex.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String relativePath = extractPathFromUrl(fileUrl);
            Path filePath = getStoragePath().resolve(relativePath);
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.error("Error deleting file locally", ex);
            return false;
        }
    }

    @Override
    public boolean fileExists(String fileUrl) {
        String relativePath = extractPathFromUrl(fileUrl);
        Path filePath = getStoragePath().resolve(relativePath);
        return Files.exists(filePath);
    }
    
    private String extractPathFromUrl(String fileUrl) {
        return fileUrl.replace(baseUrl + "/", "");
    }
} 