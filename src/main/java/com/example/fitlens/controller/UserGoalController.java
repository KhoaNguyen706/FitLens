package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.dto.request.CreateUserGoalRequest;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.UserGoalResponse;
import com.example.fitlens.mapper.UserGoalMapper;
import com.example.fitlens.security.AuthUser;
import com.example.fitlens.service.UserGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class UserGoalController {

    private final UserGoalService userGoalService;
    private final UserGoalMapper userGoalMapper;
    private final ApiResponseFactory responses;

    @GetMapping
    public ApiResponse<List<UserGoalResponse>> getGoals(@AuthenticationPrincipal AuthUser authUser) {
        return responses.ok(userGoalMapper.toResponseList(userGoalService.getGoals(authUser.id())));
    }

    @GetMapping("/active")
    public ApiResponse<UserGoalResponse> getActiveGoal(@AuthenticationPrincipal AuthUser authUser) {
        return responses.ok(userGoalMapper.toResponse(userGoalService.getActiveGoal(authUser.id())));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserGoalResponse> createGoal(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateUserGoalRequest request
    ) {
        return responses.ok(
                "Goal created successfully",
                userGoalMapper.toResponse(userGoalService.createGoal(authUser.id(), request))
        );
    }
}
