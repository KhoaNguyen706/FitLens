package com.example.fitlens.repository;

import com.example.fitlens.domain.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {

    List<UserGoal> findByUserId(Long userId);

    Optional<UserGoal> findByUserIdAndActiveTrue(Long userId);
}
