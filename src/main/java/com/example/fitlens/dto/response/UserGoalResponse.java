package com.example.fitlens.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record UserGoalResponse(
        Long id,
        Integer dailyCalorieGoal,
        Integer proteinGoalGrams,
        Integer carbsGoalGrams,
        Integer fatGoalGrams,
        BigDecimal startingWeightKg,
        BigDecimal targetWeightKg,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
