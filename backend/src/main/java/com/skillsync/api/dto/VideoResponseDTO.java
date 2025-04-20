package com.skillsync.api.dto;

import com.skillsync.api.model.Tag;
import com.skillsync.api.model.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponseDTO {
    
    private Long id;
    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private Duration duration;
    private Video.Category category;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserSummaryDTO user;
    private Set<String> tags;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    
    public static VideoResponseDTO fromEntity(Video video) {
        return VideoResponseDTO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .duration(video.getDuration())
                .category(video.getCategory())
                .published(video.isPublished())
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .user(UserSummaryDTO.fromUser(video.getUser()))
                .tags(video.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .viewCount((long) video.getViews().size())
                .likeCount((long) video.getLikes().size())
                .commentCount((long) video.getComments().size())
                .build();
    }
} 