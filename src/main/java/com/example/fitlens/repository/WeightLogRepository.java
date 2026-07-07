package com.example.fitlens.repository;

import com.example.fitlens.domain.entity.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {

    List<WeightLog> findByUserIdOrderByLoggedAtDesc(Long userId);

    Optional<WeightLog> findFirstByUserIdOrderByLoggedAtDesc(Long userId);
}
