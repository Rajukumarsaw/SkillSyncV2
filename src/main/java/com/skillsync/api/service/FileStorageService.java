package com.skillsync.api.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    
    /**
     * Stores a file and returns the URL to access it
     *
     * @param file The file to store
     * @param folderPath The folder path within the storage
     * @param fileName The name to use for the file
     * @return The URL to access the stored file
     * @throws IOException If there is an error storing the file
     */
    String storeFile(MultipartFile file, String folderPath, String fileName) throws IOException;
    
    /**
     * Deletes a file from storage
     *
     * @param fileUrl The URL of the file to delete
     * @return true if the file was deleted, false otherwise
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * Checks if a file exists in the storage
     *
     * @param fileUrl The URL of the file to check
     * @return true if the file exists, false otherwise
     */
    boolean fileExists(String fileUrl);
} 