package com.example.fitlens.dto.response;

import com.example.fitlens.domain.enums.MealType;

public record MealEstimateResponse(
        String mealName,
        MealType mealType,
        Integer calories,
        Integer confidencePercent,
        String notes,
        boolean aiGenerated
) {
}
