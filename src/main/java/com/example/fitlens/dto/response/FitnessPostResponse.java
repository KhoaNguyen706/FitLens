package com.example.fitlens.dto.response;

import com.example.fitlens.domain.enums.PostVisibility;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record FitnessPostResponse(
        Long id,
        Long authorId,
        String authorDisplayName,
        Long mealEntryId,
        String mealName,
        Integer mealCalories,
        String caption,
        PostVisibility visibility,
        Instant createdAt,
        boolean hasPhoto,
        Map<String, Long> reactionCounts,
        String myReaction,
        List<PostReactionResponse> reactions
) {
}
