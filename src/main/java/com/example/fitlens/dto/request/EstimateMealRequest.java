package com.example.fitlens.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EstimateMealRequest(
        @NotBlank @Size(max = 4_500_000) String photoBase64,
        String mimeType
) {
}
