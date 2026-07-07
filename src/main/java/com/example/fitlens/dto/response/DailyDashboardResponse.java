package com.example.fitlens.dto.response;

import java.time.LocalDate;
import java.util.List;

public record DailyDashboardResponse(
        LocalDate day,
        int totalCalories,
        List<MealEntryResponse> meals
) {
}
