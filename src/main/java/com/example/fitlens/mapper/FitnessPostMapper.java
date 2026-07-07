package com.example.fitlens.mapper;

import com.example.fitlens.domain.entity.FitnessPost;
import com.example.fitlens.domain.entity.MealEntry;
import com.example.fitlens.domain.entity.PostReaction;
import com.example.fitlens.dto.response.FitnessPostResponse;
import com.example.fitlens.dto.response.PostReactionResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FitnessPostMapper {

    public PostReactionResponse toReactionResponse(PostReaction reaction) {
        return new PostReactionResponse(
                reaction.getId(),
                reaction.getUser().getId(),
                reaction.getUser().getDisplayName(),
                reaction.getEmoji(),
                reaction.getCreatedAt()
        );
    }

    public FitnessPostResponse toResponse(
            FitnessPost post,
            List<PostReaction> reactions,
            Long viewerUserId,
            boolean includeReactionList
    ) {
        Map<String, Long> reactionCounts = new LinkedHashMap<>();
        String myReaction = null;

        for (PostReaction reaction : reactions) {
            reactionCounts.merge(reaction.getEmoji(), 1L, Long::sum);
            if (reaction.getUser().getId().equals(viewerUserId)) {
                myReaction = reaction.getEmoji();
            }
        }

        MealEntry meal = post.getMealEntry();
        List<PostReactionResponse> reactionList = includeReactionList
                ? reactions.stream().map(this::toReactionResponse).toList()
                : Collections.emptyList();

        return new FitnessPostResponse(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getDisplayName(),
                meal != null ? meal.getId() : null,
                meal != null ? meal.getMealName() : null,
                meal != null ? meal.getCalories() : null,
                post.getCaption(),
                post.getVisibility(),
                post.getCreatedAt(),
                post.getPhotoPath() != null && !post.getPhotoPath().isBlank(),
                reactionCounts,
                myReaction,
                reactionList
        );
    }

    public FitnessPostResponse toFeedResponse(FitnessPost post, List<PostReaction> reactions, Long viewerUserId) {
        return toResponse(post, reactions, viewerUserId, false);
    }

    public FitnessPostResponse toDetailResponse(FitnessPost post, List<PostReaction> reactions, Long viewerUserId) {
        return toResponse(post, reactions, viewerUserId, true);
    }
}
