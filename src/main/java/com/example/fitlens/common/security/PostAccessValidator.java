package com.example.fitlens.common.security;

import com.example.fitlens.domain.entity.FitnessPost;
import com.example.fitlens.domain.enums.PostVisibility;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostAccessValidator {

    private final FriendshipService friendshipService;

    public void assertCanView(Long viewerUserId, FitnessPost post) {
        Long authorId = post.getUser().getId();
        if (authorId.equals(viewerUserId)) {
            return;
        }
        if ((post.getVisibility() == PostVisibility.FRIENDS
                || post.getVisibility() == PostVisibility.CLOSE_FRIENDS)
                && friendshipService.areAcceptedFriends(viewerUserId, authorId)) {
            return;
        }
        throw new ResourceNotFoundException("Post not found: " + post.getId());
    }
}
