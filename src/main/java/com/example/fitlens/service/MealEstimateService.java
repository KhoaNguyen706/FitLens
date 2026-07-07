package com.example.fitlens.service;

import com.example.fitlens.dto.request.EstimateMealRequest;
import com.example.fitlens.dto.response.MealEstimateResponse;

public interface MealEstimateService {

    MealEstimateResponse estimateMeal(EstimateMealRequest request);
}
