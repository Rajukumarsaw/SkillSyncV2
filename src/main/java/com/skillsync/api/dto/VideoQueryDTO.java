package com.skillsync.api.dto;

import com.skillsync.api.model.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoQueryDTO {
    
    private String keyword;
    private Set<String> tags;
    private Video.Category category;
    private Long userId;
    private Integer minDurationSeconds;
    private Integer maxDurationSeconds;
    private Boolean mostViewed;
    private Boolean mostLiked;
    private Boolean mostRecent;
    private Boolean published;
} 