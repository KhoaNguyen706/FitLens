package com.example.fitlens.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateUserGoalRequest(
        @NotNull @Min(1) Integer dailyCalorieGoal,
        Integer proteinGoalGrams,
        Integer carbsGoalGrams,
        Integer fatGoalGrams,
        BigDecimal startingWeightKg,
        BigDecimal targetWeightKg,
        boolean active
) {
}
