package com.example.fitlens.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendDirectMessageRequest(
        @NotBlank @Size(max = 2000) String body
) {
}
