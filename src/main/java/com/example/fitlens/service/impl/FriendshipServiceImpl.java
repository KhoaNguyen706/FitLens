package com.example.fitlens.service.impl;

import com.example.fitlens.domain.entity.Friendship;
import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.enums.FriendshipStatus;
import com.example.fitlens.dto.request.SendFriendRequestRequest;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.FriendshipRepository;
import com.example.fitlens.repository.UserRepository;
import com.example.fitlens.service.FriendshipService;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    @Transactional
    public Friendship sendRequest(Long requesterId, SendFriendRequestRequest request) {
        User requester = userService.getById(requesterId);
        User receiver = userRepository.findByEmail(request.email().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.email()));

        if (receiver.getId().equals(requesterId)) {
            throw new IllegalArgumentException("You cannot send a friend request to yourself.");
        }

        Friendship existing = friendshipRepository.findBetweenUsers(requesterId, receiver.getId()).orElse(null);
        if (existing != null) {
            if (existing.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new IllegalArgumentException("You are already friends with this user.");
            }
            if (existing.getStatus() == FriendshipStatus.PENDING) {
                throw new IllegalArgumentException("A friend request already exists between you and this user.");
            }
            if (existing.getStatus() == FriendshipStatus.BLOCKED) {
                throw new IllegalArgumentException("Cannot send a friend request to this user.");
            }
        }

        Friendship friendship = new Friendship();
        friendship.setRequester(requester);
        friendship.setReceiver(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendship = friendshipRepository.save(friendship);
        return friendshipRepository.findByIdWithUsers(friendship.getId()).orElse(friendship);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getPendingRequests(Long userId) {
        userService.getById(userId);
        return friendshipRepository.findPendingRequestsForReceiver(
                userId,
                FriendshipStatus.PENDING
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getFriends(Long userId) {
        userService.getById(userId);
        return friendshipRepository.findAcceptedFriendships(userId, FriendshipStatus.ACCEPTED);
    }

    @Override
    @Transactional
    public Friendship acceptRequest(Long userId, Long friendshipId) {
        Friendship friendship = friendshipRepository.findByIdWithUsers(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found: " + friendshipId));

        if (!friendship.getReceiver().getId().equals(userId)) {
            throw new ResourceNotFoundException("Friend request not found: " + friendshipId);
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("This friend request is no longer pending.");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendship;
    }

    @Override
    @Transactional
    public void removeFriend(Long userId, Long friendUserId) {
        userService.getById(userId);
        Friendship friendship = friendshipRepository.findBetweenUsers(userId, friendUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship not found"));

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new IllegalArgumentException("You can only remove accepted friendships.");
        }

        friendshipRepository.delete(friendship);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areAcceptedFriends(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            return true;
        }
        return friendshipRepository.areAcceptedFriends(userId1, userId2);
    }
}
