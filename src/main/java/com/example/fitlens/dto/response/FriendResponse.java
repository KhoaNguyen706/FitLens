package com.example.fitlens.dto.response;

import java.time.Instant;

public record FriendResponse(
        Long friendshipId,
        Long friendUserId,
        String displayName,
        String email,
        Instant friendsSince
) {
}
