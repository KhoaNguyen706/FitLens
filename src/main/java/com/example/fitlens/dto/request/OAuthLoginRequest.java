package com.example.fitlens.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OAuthLoginRequest(
        @NotBlank String idToken,
        String displayName
) {
}
