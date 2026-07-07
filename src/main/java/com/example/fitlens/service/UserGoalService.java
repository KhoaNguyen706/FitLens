package com.example.fitlens.service;

import com.example.fitlens.domain.entity.UserGoal;
import com.example.fitlens.dto.request.CreateUserGoalRequest;

import java.util.List;

public interface UserGoalService {

    UserGoal getActiveGoal(Long userId);

    List<UserGoal> getGoals(Long userId);

    UserGoal createGoal(Long userId, CreateUserGoalRequest request);
}
