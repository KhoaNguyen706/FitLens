package com.example.fitlens.dto.response;

import java.time.Instant;

public record DirectMessageResponse(
        Long id,
        Long senderId,
        String senderDisplayName,
        Long receiverId,
        String body,
        boolean mine,
        Instant createdAt
) {
}
