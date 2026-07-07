package com.example.fitlens.dto.response;

import com.example.fitlens.domain.enums.FriendshipStatus;

import java.time.Instant;

public record FriendRequestResponse(
        Long id,
        UserSummaryResponse requester,
        UserSummaryResponse receiver,
        FriendshipStatus status,
        Instant createdAt
) {
}
