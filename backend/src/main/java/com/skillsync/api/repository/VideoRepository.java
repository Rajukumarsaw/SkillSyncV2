package com.skillsync.api.repository;

import com.skillsync.api.model.User;
import com.skillsync.api.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    Page<Video> findByPublishedTrue(Pageable pageable);
    
    Page<Video> findByUser(User user, Pageable pageable);
    
    Page<Video> findByUserAndPublishedTrue(User user, Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE v.title LIKE %:keyword% OR v.description LIKE %:keyword%")
    Page<Video> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT v FROM Video v JOIN v.tags t WHERE t.name IN :tagNames")
    Page<Video> findByTagNames(@Param("tagNames") Set<String> tagNames, Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE v.category = :category")
    Page<Video> findByCategory(@Param("category") Video.Category category, Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE v.createdAt < :cutoffDate AND v.published = true AND NOT EXISTS (SELECT 1 FROM View view WHERE view.video = v)")
    List<Video> findUnviewedVideosOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query(value = "SELECT v.* FROM videos v " +
            "JOIN views view ON v.id = view.video_id " +
            "WHERE view.user_id = :userId " +
            "GROUP BY v.id " +
            "ORDER BY COUNT(view.id) DESC LIMIT :limit", nativeQuery = true)
    List<Video> findMostViewedVideosByUser(@Param("userId") Long userId, @Param("limit") int limit);

    @Query(value = "SELECT v.* FROM videos v " +
            "JOIN video_tags vt ON v.id = vt.video_id " +
            "JOIN tags t ON vt.tag_id = t.id " +
            "JOIN user_progress up ON v.id = up.video_id " +
            "WHERE up.user_id = :userId AND up.completed = true " +
            "AND t.name IN (SELECT t2.name FROM videos v2 " +
                          "JOIN video_tags vt2 ON v2.id = vt2.video_id " +
                          "JOIN tags t2 ON vt2.tag_id = t2.id " +
                          "WHERE v2.id = :videoId) " +
            "AND v.id != :videoId " +
            "GROUP BY v.id " +
            "ORDER BY v.created_at DESC LIMIT :limit", nativeQuery = true)
    List<Video> findRelatedVideos(@Param("videoId") Long videoId, @Param("userId") Long userId, @Param("limit") int limit);
} 