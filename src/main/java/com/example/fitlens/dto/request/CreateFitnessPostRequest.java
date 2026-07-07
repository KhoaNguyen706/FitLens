package com.example.fitlens.dto.request;

import com.example.fitlens.domain.enums.PostVisibility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFitnessPostRequest(
        Long mealEntryId,
        @Size(max = 500) String caption,
        @NotNull PostVisibility visibility,
        @Size(max = 3_000_000) String photoBase64
) {
}
