package com.skillsync.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_progress", indexes = {
    @Index(name = "idx_progress_user_id", columnList = "user_id"),
    @Index(name = "idx_progress_video_id", columnList = "video_id")
},
uniqueConstraints = {
    @UniqueConstraint(name = "uk_progress_user_video", columnNames = {"user_id", "video_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Progress tracking
    private double progressPercentage; // 0-100
    
    private long lastPlaybackPositionSeconds;
    
    private boolean completed;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;
    
    public enum Status {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        BOOKMARKED
    }
} 