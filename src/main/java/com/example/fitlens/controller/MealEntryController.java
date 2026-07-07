package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.dto.request.CreateMealEntryRequest;
import com.example.fitlens.dto.request.UpdateMealEntryRequest;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.MealEntryResponse;
import com.example.fitlens.mapper.MealEntryMapper;
import com.example.fitlens.security.AuthUser;
import com.example.fitlens.service.MealEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealEntryController {

    private final MealEntryService mealEntryService;
    private final MealEntryMapper mealEntryMapper;
    private final ApiResponseFactory responses;

    @GetMapping
    public ApiResponse<List<MealEntryResponse>> getMealsForDay(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day
    ) {
        return responses.ok(mealEntryMapper.toResponseList(
                mealEntryService.getMealsForDay(authUser.id(), day)
        ));
    }

    @GetMapping("/{mealEntryId}")
    public ApiResponse<MealEntryResponse> getMealEntry(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long mealEntryId
    ) {
        return responses.ok(mealEntryMapper.toResponse(
                mealEntryService.getById(authUser.id(), mealEntryId)
        ));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MealEntryResponse> createMealEntry(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateMealEntryRequest request
    ) {
        return responses.ok(
                "Meal entry created successfully",
                mealEntryMapper.toResponse(mealEntryService.createMealEntry(authUser.id(), request))
        );
    }

    @PutMapping("/{mealEntryId}")
    public ApiResponse<MealEntryResponse> updateMealEntry(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long mealEntryId,
            @Valid @RequestBody UpdateMealEntryRequest request
    ) {
        return responses.ok(
                "Meal entry updated successfully",
                mealEntryMapper.toResponse(mealEntryService.updateMealEntry(authUser.id(), mealEntryId, request))
        );
    }

    @DeleteMapping("/{mealEntryId}")
    public ApiResponse<Void> deleteMealEntry(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long mealEntryId
    ) {
        mealEntryService.deleteMealEntry(authUser.id(), mealEntryId);
        return responses.message("Meal entry deleted successfully");
    }
}
