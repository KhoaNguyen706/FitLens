package com.example.fitlens.dto.request;

import com.example.fitlens.domain.enums.StorageProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCloudMealPhotoRequest(
        Long mealEntryId,
        @NotNull StorageProvider storageProvider,
        @NotBlank String storagePath
) {
}
