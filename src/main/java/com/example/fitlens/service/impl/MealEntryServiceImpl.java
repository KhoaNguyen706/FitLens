package com.example.fitlens.service.impl;

import com.example.fitlens.common.security.OwnedResourceValidator;
import com.example.fitlens.common.util.DateTimeUtils;
import com.example.fitlens.domain.entity.MealEntry;
import com.example.fitlens.domain.entity.User;
import com.example.fitlens.dto.request.CreateMealEntryRequest;
import com.example.fitlens.dto.request.UpdateMealEntryRequest;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.MealEntryRepository;
import com.example.fitlens.service.MealEntryService;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealEntryServiceImpl implements MealEntryService {

    private final UserService userService;
    private final MealEntryRepository mealEntryRepository;
    private final OwnedResourceValidator ownedResourceValidator;

    @Override
    @Transactional(readOnly = true)
    public MealEntry getById(Long userId, Long mealEntryId) {
        return getOwnedMealEntry(userId, mealEntryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealEntry> getMealsForDay(Long userId, LocalDate day) {
        userService.getById(userId);
        DateTimeUtils.DayRange range = DateTimeUtils.dayRangeUtc(day);
        return mealEntryRepository.findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(
                userId,
                range.start(),
                range.end()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public int getDailyCalorieTotal(Long userId, LocalDate day) {
        userService.getById(userId);
        DateTimeUtils.DayRange range = DateTimeUtils.dayRangeUtc(day);
        return mealEntryRepository.sumCaloriesByUserIdAndLoggedAtBetween(
                userId,
                range.start(),
                range.end()
        );
    }

    @Override
    @Transactional
    public MealEntry createMealEntry(Long userId, CreateMealEntryRequest request) {
        User user = userService.getById(userId);

        MealEntry mealEntry = new MealEntry();
        mealEntry.setUser(user);
        mealEntry.setMealName(request.mealName());
        mealEntry.setMealType(request.mealType());
        mealEntry.setCalories(request.calories());
        mealEntry.setProteinGrams(request.proteinGrams());
        mealEntry.setCarbsGrams(request.carbsGrams());
        mealEntry.setFatGrams(request.fatGrams());
        mealEntry.setNotes(request.notes());
        mealEntry.setLoggedAt(request.loggedAt() != null ? request.loggedAt() : Instant.now());

        return mealEntryRepository.save(mealEntry);
    }

    @Override
    @Transactional
    public MealEntry updateMealEntry(Long userId, Long mealEntryId, UpdateMealEntryRequest request) {
        MealEntry mealEntry = getOwnedMealEntry(userId, mealEntryId);

        mealEntry.setMealName(request.mealName());
        mealEntry.setMealType(request.mealType());
        mealEntry.setCalories(request.calories());
        mealEntry.setProteinGrams(request.proteinGrams());
        mealEntry.setCarbsGrams(request.carbsGrams());
        mealEntry.setFatGrams(request.fatGrams());
        mealEntry.setNotes(request.notes());
        mealEntry.setLoggedAt(request.loggedAt());

        return mealEntry;
    }

    @Override
    @Transactional
    public void deleteMealEntry(Long userId, Long mealEntryId) {
        MealEntry mealEntry = getOwnedMealEntry(userId, mealEntryId);
        mealEntryRepository.delete(mealEntry);
    }

    private MealEntry getOwnedMealEntry(Long userId, Long mealEntryId) {
        MealEntry mealEntry = mealEntryRepository.findById(mealEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal entry not found: " + mealEntryId));

        ownedResourceValidator.assertSameOwner(mealEntry.getUser().getId(), userId, "Meal entry", mealEntryId);
        return mealEntry;
    }
}
