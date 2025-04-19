package com.skillsync.api.repository;

import com.skillsync.api.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(String name);
    
    List<Tag> findByNameIn(Set<String> names);
    
    boolean existsByName(String name);
    
    @Query("SELECT t FROM Tag t JOIN t.videos v GROUP BY t ORDER BY COUNT(v) DESC LIMIT :limit")
    List<Tag> findMostPopularTags(@Param("limit") int limit);
    
    @Query("SELECT t FROM Tag t JOIN t.videos v WHERE v.id = :videoId")
    List<Tag> findByVideoId(@Param("videoId") Long videoId);
    
    @Query(value = "SELECT t.* FROM tags t " +
            "JOIN video_tags vt ON t.id = vt.tag_id " +
            "JOIN videos v ON vt.video_id = v.id " +
            "JOIN user_progress up ON v.id = up.video_id " +
            "WHERE up.user_id = :userId AND up.completed = true " +
            "GROUP BY t.id " +
            "ORDER BY COUNT(v.id) DESC LIMIT :limit", nativeQuery = true)
    List<Tag> findUserPreferredTags(@Param("userId") Long userId, @Param("limit") int limit);
} 