package com.example.fitlens.repository;

import com.example.fitlens.domain.entity.FitnessPost;
import com.example.fitlens.domain.enums.PostVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FitnessPostRepository extends JpaRepository<FitnessPost, Long> {

    @Query("""
            SELECT p FROM FitnessPost p
            JOIN FETCH p.user
            LEFT JOIN FETCH p.mealEntry
            WHERE p.visibility IN :visibilities
              AND p.user.id IN :authorIds
            ORDER BY p.createdAt DESC
            """)
    List<FitnessPost> findFeedPosts(
            @Param("authorIds") List<Long> authorIds,
            @Param("visibilities") List<PostVisibility> visibilities
    );

    @Query("""
            SELECT p FROM FitnessPost p
            JOIN FETCH p.user
            LEFT JOIN FETCH p.mealEntry
            WHERE p.id = :postId
            """)
    Optional<FitnessPost> findByIdWithDetails(@Param("postId") Long postId);
}
