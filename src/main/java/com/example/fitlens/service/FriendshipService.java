package com.example.fitlens.service;

import com.example.fitlens.domain.entity.Friendship;
import com.example.fitlens.dto.request.SendFriendRequestRequest;

import java.util.List;

public interface FriendshipService {

    Friendship sendRequest(Long requesterId, SendFriendRequestRequest request);

    List<Friendship> getPendingRequests(Long userId);

    List<Friendship> getFriends(Long userId);

    Friendship acceptRequest(Long userId, Long friendshipId);

    void removeFriend(Long userId, Long friendUserId);

    boolean areAcceptedFriends(Long userId1, Long userId2);
}
