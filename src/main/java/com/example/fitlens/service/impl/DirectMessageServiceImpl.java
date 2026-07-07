package com.example.fitlens.service.impl;

import com.example.fitlens.domain.entity.DirectMessage;
import com.example.fitlens.domain.entity.User;
import com.example.fitlens.dto.request.SendDirectMessageRequest;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.DirectMessageRepository;
import com.example.fitlens.service.DirectMessageService;
import com.example.fitlens.service.FriendshipService;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectMessageServiceImpl implements DirectMessageService {

    private final UserService userService;
    private final FriendshipService friendshipService;
    private final DirectMessageRepository directMessageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DirectMessage> getConversation(Long userId, Long friendUserId) {
        assertCanChat(userId, friendUserId);
        return directMessageRepository.findConversation(userId, friendUserId);
    }

    @Override
    @Transactional
    public DirectMessage sendMessage(Long userId, Long friendUserId, SendDirectMessageRequest request) {
        User sender = userService.getById(userId);
        User receiver = userService.getById(friendUserId);
        assertCanChat(userId, friendUserId);

        DirectMessage message = new DirectMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setBody(request.body().trim());
        return directMessageRepository.save(message);
    }

    private void assertCanChat(Long userId, Long friendUserId) {
        userService.getById(friendUserId);
        if (userId.equals(friendUserId)) {
            throw new IllegalArgumentException("You cannot message yourself.");
        }
        if (!friendshipService.areAcceptedFriends(userId, friendUserId)) {
            throw new ResourceNotFoundException("Friend not found: " + friendUserId);
        }
    }
}
