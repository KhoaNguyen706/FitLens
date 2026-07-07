package com.example.fitlens.dto.response;

import com.example.fitlens.domain.enums.PhotoSource;
import com.example.fitlens.domain.enums.StorageProvider;

import java.time.Instant;

public record MealPhotoResponse(
        Long id,
        Long mealEntryId,
        String localUri,
        String localAssetId,
        StorageProvider storageProvider,
        String storagePath,
        PhotoSource photoSource,
        Instant createdAt
) {
}
