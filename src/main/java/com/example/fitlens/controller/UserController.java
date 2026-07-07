package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.UserResponse;
import com.example.fitlens.mapper.UserMapper;
import com.example.fitlens.security.AuthUser;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ApiResponseFactory responses;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal AuthUser authUser) {
        return responses.ok(userMapper.toResponse(userService.getById(authUser.id())));
    }
}
