package com.example.fitlens.dto.response;

import java.time.Instant;

public record PostReactionResponse(
        Long id,
        Long userId,
        String displayName,
        String emoji,
        Instant createdAt
) {
}
