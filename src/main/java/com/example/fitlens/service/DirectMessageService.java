package com.example.fitlens.service;

import com.example.fitlens.domain.entity.DirectMessage;
import com.example.fitlens.dto.request.SendDirectMessageRequest;

import java.util.List;

public interface DirectMessageService {

    List<DirectMessage> getConversation(Long userId, Long friendUserId);

    DirectMessage sendMessage(Long userId, Long friendUserId, SendDirectMessageRequest request);
}
