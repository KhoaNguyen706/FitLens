package com.example.fitlens.repository;

import com.example.fitlens.domain.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    List<PostReaction> findByPostIdOrderByCreatedAtAsc(Long postId);

    @Query("""
            SELECT r FROM PostReaction r
            JOIN FETCH r.user
            WHERE r.post.id = :postId
            ORDER BY r.createdAt ASC
            """)
    List<PostReaction> findByPostIdWithUsers(@Param("postId") Long postId);

    Optional<PostReaction> findByPostIdAndUserId(Long postId, Long userId);

    void deleteByPostIdAndUserId(Long postId, Long userId);
}
