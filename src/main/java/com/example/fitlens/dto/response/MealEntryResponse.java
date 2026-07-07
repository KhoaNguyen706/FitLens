package com.example.fitlens.dto.response;

import com.example.fitlens.domain.enums.MealType;

import java.math.BigDecimal;
import java.time.Instant;

public record MealEntryResponse(
        Long id,
        String mealName,
        MealType mealType,
        Integer calories,
        BigDecimal proteinGrams,
        BigDecimal carbsGrams,
        BigDecimal fatGrams,
        String notes,
        Instant loggedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
