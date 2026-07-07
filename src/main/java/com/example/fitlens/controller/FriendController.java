package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.dto.request.SendDirectMessageRequest;
import com.example.fitlens.dto.request.SendFriendRequestRequest;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.DirectMessageResponse;
import com.example.fitlens.dto.response.FriendRequestResponse;
import com.example.fitlens.dto.response.FriendResponse;
import com.example.fitlens.mapper.DirectMessageMapper;
import com.example.fitlens.mapper.FriendshipMapper;
import com.example.fitlens.security.AuthUser;
import com.example.fitlens.service.DirectMessageService;
import com.example.fitlens.service.FriendshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendshipService friendshipService;
    private final DirectMessageService directMessageService;
    private final FriendshipMapper friendshipMapper;
    private final DirectMessageMapper directMessageMapper;
    private final ApiResponseFactory responses;

    @GetMapping
    public ApiResponse<List<FriendResponse>> getFriends(@AuthenticationPrincipal AuthUser authUser) {
        return responses.ok(friendshipMapper.toFriendResponseList(
                friendshipService.getFriends(authUser.id()),
                authUser.id()
        ));
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FriendRequestResponse> sendRequest(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody SendFriendRequestRequest request
    ) {
        return responses.ok(
                "Friend request sent",
                friendshipMapper.toRequestResponse(
                        friendshipService.sendRequest(authUser.id(), request)
                )
        );
    }

    @GetMapping("/requests")
    public ApiResponse<List<FriendRequestResponse>> getPendingRequests(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return responses.ok(friendshipMapper.toRequestResponseList(
                friendshipService.getPendingRequests(authUser.id())
        ));
    }

    @PostMapping("/requests/{id}/accept")
    public ApiResponse<FriendRequestResponse> acceptRequest(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long id
    ) {
        return responses.ok(
                "Friend request accepted",
                friendshipMapper.toRequestResponse(
                        friendshipService.acceptRequest(authUser.id(), id)
                )
        );
    }

    @DeleteMapping("/{friendId}")
    public ApiResponse<Void> removeFriend(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long friendId
    ) {
        friendshipService.removeFriend(authUser.id(), friendId);
        return responses.message("Friend removed");
    }

    @GetMapping("/{friendId}/messages")
    public ApiResponse<List<DirectMessageResponse>> getMessages(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long friendId
    ) {
        return responses.ok(directMessageMapper.toResponseList(
                directMessageService.getConversation(authUser.id(), friendId),
                authUser.id()
        ));
    }

    @PostMapping("/{friendId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DirectMessageResponse> sendMessage(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long friendId,
            @Valid @RequestBody SendDirectMessageRequest request
    ) {
        return responses.ok(
                "Message sent",
                directMessageMapper.toResponse(
                        directMessageService.sendMessage(authUser.id(), friendId, request),
                        authUser.id()
                )
        );
    }
}
