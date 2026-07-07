package com.example.fitlens.mapper;

import com.example.fitlens.domain.entity.MealEntry;
import com.example.fitlens.dto.response.MealEntryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealEntryMapper {

    public MealEntryResponse toResponse(MealEntry mealEntry) {
        return new MealEntryResponse(
                mealEntry.getId(),
                mealEntry.getMealName(),
                mealEntry.getMealType(),
                mealEntry.getCalories(),
                mealEntry.getProteinGrams(),
                mealEntry.getCarbsGrams(),
                mealEntry.getFatGrams(),
                mealEntry.getNotes(),
                mealEntry.getLoggedAt(),
                mealEntry.getCreatedAt(),
                mealEntry.getUpdatedAt()
        );
    }

    public List<MealEntryResponse> toResponseList(List<MealEntry> mealEntries) {
        return mealEntries.stream().map(this::toResponse).toList();
    }
}
