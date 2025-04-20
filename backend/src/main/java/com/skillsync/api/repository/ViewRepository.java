package com.skillsync.api.repository;

import com.skillsync.api.model.User;
import com.skillsync.api.model.Video;
import com.skillsync.api.model.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {
    
    List<View> findByVideoOrderByCreatedAtDesc(Video video);
    
    List<View> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<View> findTopByUserAndVideoOrderByCreatedAtDesc(User user, Video video);
    
    long countByVideo(Video video);
    
    @Query("SELECT COUNT(v) FROM View v WHERE v.video.id = :videoId")
    Long countViewsForVideo(@Param("videoId") Long videoId);
    
    @Query("SELECT COUNT(v) FROM View v WHERE v.video.id = :videoId AND v.createdAt > :startDate")
    Long countViewsForVideoSince(@Param("videoId") Long videoId, @Param("startDate") LocalDateTime startDate);
    
    @Query(value = "SELECT DATE(v.created_at) as date, COUNT(*) as count FROM views v " +
            "WHERE v.video_id = :videoId AND v.created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(v.created_at) ORDER BY date", nativeQuery = true)
    List<Object[]> getViewsPerDayForVideo(@Param("videoId") Long videoId, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query(value = "SELECT video_id, COUNT(*) as count FROM views " +
            "GROUP BY video_id ORDER BY count DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> findMostViewedVideos(@Param("limit") int limit);
    
    @Query(value = "SELECT video_id FROM views " +
            "WHERE user_id = :userId " +
            "GROUP BY video_id " +
            "ORDER BY MAX(created_at) DESC LIMIT :limit", nativeQuery = true)
    List<Long> findRecentlyViewedVideoIdsByUser(@Param("userId") Long userId, @Param("limit") int limit);
} 