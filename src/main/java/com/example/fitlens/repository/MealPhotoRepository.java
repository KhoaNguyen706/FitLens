package com.example.fitlens.repository;

import com.example.fitlens.domain.entity.MealPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MealPhotoRepository extends JpaRepository<MealPhoto, Long> {

    @Query("""
            select p from MealPhoto p
            left join fetch p.mealEntry
            where p.user.id = :userId
            order by p.createdAt desc
            """)
    List<MealPhoto> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("""
            select p from MealPhoto p
            left join fetch p.mealEntry
            where p.mealEntry.id = :mealEntryId
            """)
    Optional<MealPhoto> findByMealEntryId(@Param("mealEntryId") Long mealEntryId);

    @Query("""
            select p from MealPhoto p
            left join fetch p.mealEntry
            where p.id = :mealPhotoId
            """)
    Optional<MealPhoto> findFetchedById(@Param("mealPhotoId") Long mealPhotoId);

    List<MealPhoto> findByMealEntryIdIn(List<Long> mealEntryIds);
}
