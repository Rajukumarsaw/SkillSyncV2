package com.skillsync.api.repository;

import com.skillsync.api.model.User;
import com.skillsync.api.model.UserProgress;
import com.skillsync.api.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    
    Optional<UserProgress> findByUserAndVideo(User user, Video video);
    
    List<UserProgress> findByUser(User user);
    
    List<UserProgress> findByUserAndCompleted(User user, boolean completed);
    
    List<UserProgress> findByUserAndStatus(User user, UserProgress.Status status);
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId AND up.status = 'IN_PROGRESS'")
    List<UserProgress> findInProgressByUserId(@Param("userId") Long userId);
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId AND up.status = 'BOOKMARKED'")
    List<UserProgress> findBookmarkedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.user.id = :userId AND up.completed = true")
    Long countCompletedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT AVG(up.progressPercentage) FROM UserProgress up WHERE up.user.id = :userId")
    Double getAverageProgressByUserId(@Param("userId") Long userId);
    
    @Query(value = "SELECT v.category, COUNT(*) as count FROM user_progress up " +
            "JOIN videos v ON up.video_id = v.id " +
            "WHERE up.user_id = :userId AND up.completed = true " +
            "GROUP BY v.category ORDER BY count DESC", nativeQuery = true)
    List<Object[]> getCompletedVideoCountByCategory(@Param("userId") Long userId);
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId ORDER BY up.updatedAt DESC")
    Page<UserProgress> findRecentProgressByUserId(@Param("userId") Long userId, Pageable pageable);
} 