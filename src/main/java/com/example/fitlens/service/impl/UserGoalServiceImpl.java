package com.example.fitlens.service.impl;

import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.entity.UserGoal;
import com.example.fitlens.dto.request.CreateUserGoalRequest;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.UserGoalRepository;
import com.example.fitlens.service.UserGoalService;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGoalServiceImpl implements UserGoalService {

    private final UserService userService;
    private final UserGoalRepository userGoalRepository;

    @Override
    @Transactional(readOnly = true)
    public UserGoal getActiveGoal(Long userId) {
        userService.getById(userId);

        return userGoalRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Active goal not found for user: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGoal> getGoals(Long userId) {
        userService.getById(userId);
        return userGoalRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public UserGoal createGoal(Long userId, CreateUserGoalRequest request) {
        User user = userService.getById(userId);

        if (request.active()) {
            deactivateGoals(userId);
        }

        UserGoal goal = new UserGoal();
        goal.setUser(user);
        goal.setDailyCalorieGoal(request.dailyCalorieGoal());
        goal.setProteinGoalGrams(request.proteinGoalGrams());
        goal.setCarbsGoalGrams(request.carbsGoalGrams());
        goal.setFatGoalGrams(request.fatGoalGrams());
        goal.setStartingWeightKg(request.startingWeightKg());
        goal.setTargetWeightKg(request.targetWeightKg());
        goal.setActive(request.active());

        return userGoalRepository.save(goal);
    }

    private void deactivateGoals(Long userId) {
        userGoalRepository.findByUserId(userId).stream()
                .filter(goal -> Boolean.TRUE.equals(goal.getActive()))
                .forEach(goal -> goal.setActive(false));
    }
}
