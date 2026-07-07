package com.example.fitlens.dto.response;

public record AuthResponse(
        String token,
        String refreshToken,
        UserResponse user
) {
}
