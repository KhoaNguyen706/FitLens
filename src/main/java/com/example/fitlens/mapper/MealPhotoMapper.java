package com.example.fitlens.mapper;

import com.example.fitlens.domain.entity.MealPhoto;
import com.example.fitlens.dto.response.MealPhotoResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealPhotoMapper {

    public MealPhotoResponse toResponse(MealPhoto mealPhoto) {
        return new MealPhotoResponse(
                mealPhoto.getId(),
                mealPhoto.getMealEntry() != null ? mealPhoto.getMealEntry().getId() : null,
                mealPhoto.getLocalUri(),
                mealPhoto.getLocalAssetId(),
                mealPhoto.getStorageProvider(),
                mealPhoto.getStoragePath(),
                mealPhoto.getPhotoSource(),
                mealPhoto.getCreatedAt()
        );
    }

    public List<MealPhotoResponse> toResponseList(List<MealPhoto> mealPhotos) {
        return mealPhotos.stream().map(this::toResponse).toList();
    }
}
