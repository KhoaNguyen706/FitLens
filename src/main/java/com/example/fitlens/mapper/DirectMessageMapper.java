package com.example.fitlens.mapper;

import com.example.fitlens.domain.entity.DirectMessage;
import com.example.fitlens.dto.response.DirectMessageResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DirectMessageMapper {

    public DirectMessageResponse toResponse(DirectMessage message, Long viewerUserId) {
        return new DirectMessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getDisplayName(),
                message.getReceiver().getId(),
                message.getBody(),
                message.getSender().getId().equals(viewerUserId),
                message.getCreatedAt()
        );
    }

    public List<DirectMessageResponse> toResponseList(List<DirectMessage> messages, Long viewerUserId) {
        return messages.stream().map(m -> toResponse(m, viewerUserId)).toList();
    }
}
