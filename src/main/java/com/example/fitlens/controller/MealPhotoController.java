package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.dto.request.CreateCloudMealPhotoRequest;
import com.example.fitlens.dto.request.CreateLocalMealPhotoRequest;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.MealPhotoResponse;
import com.example.fitlens.mapper.MealPhotoMapper;
import com.example.fitlens.security.AuthUser;
import com.example.fitlens.service.MealPhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/meal-photos")
@RequiredArgsConstructor
public class MealPhotoController {

    private final MealPhotoService mealPhotoService;
    private final MealPhotoMapper mealPhotoMapper;
    private final ApiResponseFactory responses;

    @GetMapping
    public ApiResponse<List<MealPhotoResponse>> getPhotos(@AuthenticationPrincipal AuthUser authUser) {
        return responses.ok(mealPhotoMapper.toResponseList(
                mealPhotoService.getPhotosForUser(authUser.id())
        ));
    }

    @GetMapping("/meal/{mealEntryId}")
    public ApiResponse<MealPhotoResponse> getPhotoForMeal(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long mealEntryId
    ) {
        return responses.ok(mealPhotoMapper.toResponse(
                mealPhotoService.getPhotoForMeal(authUser.id(), mealEntryId)
        ));
    }

    @PostMapping("/local")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MealPhotoResponse> createLocalPhoto(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateLocalMealPhotoRequest request
    ) {
        return responses.ok(
                "Local meal photo saved successfully",
                mealPhotoMapper.toResponse(mealPhotoService.createLocalPhoto(authUser.id(), request))
        );
    }

    @PostMapping("/cloud")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MealPhotoResponse> createCloudPhoto(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateCloudMealPhotoRequest request
    ) {
        return responses.ok(
                "Cloud meal photo saved successfully",
                mealPhotoMapper.toResponse(mealPhotoService.createCloudPhoto(authUser.id(), request))
        );
    }

    @DeleteMapping("/{mealPhotoId}")
    public ApiResponse<Void> deletePhoto(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long mealPhotoId
    ) {
        mealPhotoService.deletePhoto(authUser.id(), mealPhotoId);
        return responses.message("Meal photo deleted successfully");
    }
}
