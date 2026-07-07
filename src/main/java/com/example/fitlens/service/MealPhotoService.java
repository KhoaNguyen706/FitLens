package com.example.fitlens.service;

import com.example.fitlens.domain.entity.MealPhoto;
import com.example.fitlens.dto.request.CreateCloudMealPhotoRequest;
import com.example.fitlens.dto.request.CreateLocalMealPhotoRequest;

import java.util.List;

public interface MealPhotoService {

    MealPhoto getById(Long userId, Long mealPhotoId);

    List<MealPhoto> getPhotosForUser(Long userId);

    MealPhoto getPhotoForMeal(Long userId, Long mealEntryId);

    MealPhoto createLocalPhoto(Long userId, CreateLocalMealPhotoRequest request);

    MealPhoto createCloudPhoto(Long userId, CreateCloudMealPhotoRequest request);

    void deletePhoto(Long userId, Long mealPhotoId);
}
