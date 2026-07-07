package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.dto.request.EstimateMealRequest;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.MealEstimateResponse;
import com.example.fitlens.service.MealEstimateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class MealEstimateController {

    private final MealEstimateService mealEstimateService;
    private final ApiResponseFactory responses;

    @PostMapping("/meal-estimate")
    public ApiResponse<MealEstimateResponse> estimateMeal(
            @Valid @RequestBody EstimateMealRequest request
    ) {
        return responses.ok("Meal estimate created", mealEstimateService.estimateMeal(request));
    }
}
