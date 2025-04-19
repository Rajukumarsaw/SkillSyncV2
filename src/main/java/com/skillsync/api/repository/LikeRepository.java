package com.skillsync.api.repository;

import com.skillsync.api.model.Like;
import com.skillsync.api.model.User;
import com.skillsync.api.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByUserAndVideo(User user, Video video);
    
    List<Like> findByUser(User user);
    
    List<Like> findByVideo(Video video);
    
    boolean existsByUserAndVideo(User user, Video video);
    
    long countByVideo(Video video);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.video.id = :videoId")
    Long countLikesForVideo(@Param("videoId") Long videoId);
    
    @Query(value = "SELECT v.id FROM videos v " +
            "JOIN likes l ON v.id = l.video_id " +
            "WHERE l.user_id = :userId", nativeQuery = true)
    List<Long> findVideoIdsLikedByUser(@Param("userId") Long userId);
} 