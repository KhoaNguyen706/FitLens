package com.example.fitlens.mapper;

import com.example.fitlens.domain.entity.Friendship;
import com.example.fitlens.domain.entity.User;
import com.example.fitlens.dto.response.FriendRequestResponse;
import com.example.fitlens.dto.response.FriendResponse;
import com.example.fitlens.dto.response.UserSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FriendshipMapper {

    public UserSummaryResponse toUserSummary(User user) {
        return new UserSummaryResponse(user.getId(), user.getDisplayName(), user.getEmail());
    }

    public FriendRequestResponse toRequestResponse(Friendship friendship) {
        return new FriendRequestResponse(
                friendship.getId(),
                toUserSummary(friendship.getRequester()),
                toUserSummary(friendship.getReceiver()),
                friendship.getStatus(),
                friendship.getCreatedAt()
        );
    }

    public List<FriendRequestResponse> toRequestResponseList(List<Friendship> friendships) {
        return friendships.stream().map(this::toRequestResponse).toList();
    }

    public FriendResponse toFriendResponse(Friendship friendship, Long currentUserId) {
        User friend = friendship.getRequester().getId().equals(currentUserId)
                ? friendship.getReceiver()
                : friendship.getRequester();
        return new FriendResponse(
                friendship.getId(),
                friend.getId(),
                friend.getDisplayName(),
                friend.getEmail(),
                friendship.getUpdatedAt()
        );
    }

    public List<FriendResponse> toFriendResponseList(List<Friendship> friendships, Long currentUserId) {
        return friendships.stream()
                .map(f -> toFriendResponse(f, currentUserId))
                .toList();
    }
}
