package com.example.fitlens.repository;

import com.example.fitlens.domain.entity.MealEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

    List<MealEntry> findByUserIdOrderByLoggedAtDesc(Long userId);

    List<MealEntry> findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(
            Long userId,
            Instant start,
            Instant end
    );

    @Query("""
            select coalesce(sum(m.calories), 0)
            from MealEntry m
            where m.user.id = :userId
              and m.loggedAt >= :start
              and m.loggedAt < :end
            """)
    int sumCaloriesByUserIdAndLoggedAtBetween(
            @Param("userId") Long userId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}
