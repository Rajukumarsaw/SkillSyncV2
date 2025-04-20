package com.skillsync.api.service;

import com.skillsync.api.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
@Profile("aws")
@RequiredArgsConstructor
@Slf4j
public class AWSS3FileStorageService implements FileStorageService {

    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.public-endpoint}")
    private String publicEndpoint;

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
            
            // Build the full key path including folder
            String fullPath = folderPath + "/" + fileName;
            if (fullPath.startsWith("/")) {
                fullPath = fullPath.substring(1);
            }
            
            // Create a PutObjectRequest
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fullPath)
                    .contentType(file.getContentType())
                    .build();
            
            // Upload the file
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            log.info("Successfully uploaded file to S3: {}", fullPath);
            
            // Return the URL to access the file
            return publicEndpoint + "/" + fullPath;
        } catch (S3Exception e) {
            log.error("Error uploading file to S3", e);
            throw new FileStorageException("Failed to store file in S3: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            // Extract the key from the file URL
            String key = extractKeyFromUrl(fileUrl);
            
            // Create a DeleteObjectRequest
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            // Delete the file
            s3Client.deleteObject(request);
            
            log.info("Successfully deleted file from S3: {}", key);
            return true;
        } catch (S3Exception e) {
            log.error("Error deleting file from S3", e);
            return false;
        }
    }

    @Override
    public boolean fileExists(String fileUrl) {
        try {
            // Extract the key from the file URL
            String key = extractKeyFromUrl(fileUrl);
            
            // Create a HeadObjectRequest
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            // Check if the file exists
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error checking if file exists in S3", e);
            return false;
        }
    }
    
    private String extractKeyFromUrl(String fileUrl) {
        return fileUrl.replace(publicEndpoint + "/", "");
    }
} 