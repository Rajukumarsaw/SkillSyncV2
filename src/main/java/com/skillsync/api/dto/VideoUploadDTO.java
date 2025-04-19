package com.skillsync.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadDTO {
    
    @NotNull(message = "Video file is required")
    private MultipartFile videoFile;
    
    private MultipartFile thumbnailFile;
    
    @NotNull(message = "Video metadata is required")
    private VideoRequestDTO metadata;
} 