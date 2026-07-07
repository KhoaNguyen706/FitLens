package com.example.fitlens.unit.service;

import com.example.fitlens.common.security.OwnedResourceValidator;
import com.example.fitlens.domain.entity.MealEntry;
import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.enums.MealType;
import com.example.fitlens.dto.request.CreateMealEntryRequest;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.MealEntryRepository;
import com.example.fitlens.service.UserService;
import com.example.fitlens.service.impl.MealEntryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MealEntryServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private MealEntryRepository mealEntryRepository;

    @Mock
    private OwnedResourceValidator ownedResourceValidator;

    @InjectMocks
    private MealEntryServiceImpl mealEntryService;

    @Test
    void getDailyCalorieTotal_returnsSumFromRepository() {
        User user = new User();
        user.setId(1L);

        LocalDate day = LocalDate.of(2026, 7, 1);
        Instant start = day.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = day.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        when(userService.getById(1L)).thenReturn(user);
        when(mealEntryRepository.sumCaloriesByUserIdAndLoggedAtBetween(1L, start, end)).thenReturn(750);

        int total = mealEntryService.getDailyCalorieTotal(1L, day);

        assertThat(total).isEqualTo(750);
    }

    @Test
    void deleteMealEntry_throwsWhenMealBelongsToAnotherUser() {
        User owner = new User();
        owner.setId(2L);

        MealEntry mealEntry = new MealEntry();
        mealEntry.setId(10L);
        mealEntry.setUser(owner);

        when(mealEntryRepository.findById(10L)).thenReturn(Optional.of(mealEntry));
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Meal entry not found: 10"))
                .when(ownedResourceValidator)
                .assertSameOwner(2L, 1L, "Meal entry", 10L);

        assertThatThrownBy(() -> mealEntryService.deleteMealEntry(1L, 10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createMealEntry_savesMealForUser() {
        User user = new User();
        user.setId(1L);

        CreateMealEntryRequest request = new CreateMealEntryRequest(
                "Chicken rice bowl",
                MealType.LUNCH,
                750,
                BigDecimal.valueOf(45),
                BigDecimal.valueOf(80),
                BigDecimal.valueOf(20),
                null,
                Instant.parse("2026-07-01T12:30:00Z")
        );

        MealEntry savedMeal = new MealEntry();
        savedMeal.setId(5L);
        savedMeal.setMealName("Chicken rice bowl");
        savedMeal.setMealType(MealType.LUNCH);
        savedMeal.setCalories(750);

        when(userService.getById(1L)).thenReturn(user);
        when(mealEntryRepository.save(any(MealEntry.class))).thenReturn(savedMeal);

        MealEntry result = mealEntryService.createMealEntry(1L, request);

        assertThat(result.getMealName()).isEqualTo("Chicken rice bowl");
        assertThat(result.getCalories()).isEqualTo(750);
        verify(mealEntryRepository).save(any(MealEntry.class));
    }
}
