package com.example.fitlens.dto.response;

public record UserSummaryResponse(
        Long id,
        String displayName,
        String email
) {
}
