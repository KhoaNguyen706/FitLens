package com.example.fitlens.service.impl;

import com.example.fitlens.dto.response.DailyDashboardResponse;
import com.example.fitlens.dto.response.MealEntryResponse;
import com.example.fitlens.mapper.MealEntryMapper;
import com.example.fitlens.service.DashboardService;
import com.example.fitlens.service.MealEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MealEntryService mealEntryService;
    private final MealEntryMapper mealEntryMapper;

    @Override
    @Transactional(readOnly = true)
    public DailyDashboardResponse getTodayDashboard(Long userId, LocalDate day) {
        LocalDate targetDay = day != null ? day : LocalDate.now();

        List<MealEntryResponse> meals = mealEntryMapper.toResponseList(
                mealEntryService.getMealsForDay(userId, targetDay)
        );
        int totalCalories = mealEntryService.getDailyCalorieTotal(userId, targetDay);

        return new DailyDashboardResponse(targetDay, totalCalories, meals);
    }
}
