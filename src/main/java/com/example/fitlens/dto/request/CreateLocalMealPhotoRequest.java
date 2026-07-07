package com.example.fitlens.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateLocalMealPhotoRequest(
        Long mealEntryId,
        @NotBlank String localUri,
        String localAssetId
) {
}
