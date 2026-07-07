package com.example.fitlens.dto.request;

import com.example.fitlens.domain.enums.MealType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateMealEntryRequest(
        @NotBlank String mealName,
        @NotNull MealType mealType,
        @NotNull @Min(0) Integer calories,
        BigDecimal proteinGrams,
        BigDecimal carbsGrams,
        BigDecimal fatGrams,
        String notes,
        @NotNull Instant loggedAt
) {
}
