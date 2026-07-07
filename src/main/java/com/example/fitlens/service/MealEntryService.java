package com.example.fitlens.service;

import com.example.fitlens.domain.entity.MealEntry;
import com.example.fitlens.dto.request.CreateMealEntryRequest;
import com.example.fitlens.dto.request.UpdateMealEntryRequest;

import java.time.LocalDate;
import java.util.List;

public interface MealEntryService {

    MealEntry getById(Long userId, Long mealEntryId);

    List<MealEntry> getMealsForDay(Long userId, LocalDate day);

    int getDailyCalorieTotal(Long userId, LocalDate day);

    MealEntry createMealEntry(Long userId, CreateMealEntryRequest request);

    MealEntry updateMealEntry(Long userId, Long mealEntryId, UpdateMealEntryRequest request);

    void deleteMealEntry(Long userId, Long mealEntryId);
}
