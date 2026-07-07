package com.example.fitlens.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddPostReactionRequest(
        @NotBlank @Size(max = 16) String emoji
) {
}
