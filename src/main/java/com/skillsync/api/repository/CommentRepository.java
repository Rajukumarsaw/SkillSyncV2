package com.skillsync.api.repository;

import com.skillsync.api.model.Comment;
import com.skillsync.api.model.User;
import com.skillsync.api.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    Page<Comment> findByVideo(Video video, Pageable pageable);
    
    Page<Comment> findByUser(User user, Pageable pageable);
    
    Page<Comment> findByVideoAndParentIsNull(Video video, Pageable pageable);
    
    List<Comment> findByParentId(Long parentId);
    
    @Query("SELECT c FROM Comment c WHERE c.video.id = :videoId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findTopLevelCommentsByVideoId(@Param("videoId") Long videoId, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.video.id = :videoId")
    Long countCommentsForVideo(@Param("videoId") Long videoId);
} 