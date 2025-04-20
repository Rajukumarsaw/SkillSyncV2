package com.skillsync.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "videos", indexes = {
    @Index(name = "idx_video_title", columnList = "title"),
    @Index(name = "idx_video_user_id", columnList = "user_id"),
    @Index(name = "idx_video_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    @NotBlank
    private String videoUrl;

    private String thumbnailUrl;

    private Duration duration;

    @Enumerated(EnumType.STRING)
    private Category category;

    private boolean published = true;

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

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<View> views = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserProgress> progress = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "video_tags",
        joinColumns = @JoinColumn(name = "video_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    public enum Category {
        PROGRAMMING,
        DATA_SCIENCE,
        WEB_DEVELOPMENT,
        MOBILE_DEVELOPMENT,
        DEVOPS,
        CLOUD_COMPUTING,
        ARTIFICIAL_INTELLIGENCE,
        MACHINE_LEARNING,
        CYBERSECURITY,
        BLOCKCHAIN,
        GAME_DEVELOPMENT,
        UI_UX_DESIGN,
        OTHER
    }
} 