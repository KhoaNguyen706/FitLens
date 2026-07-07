package com.example.fitlens.mapper;

import com.example.fitlens.domain.entity.UserGoal;
import com.example.fitlens.dto.response.UserGoalResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGoalMapper {

    public UserGoalResponse toResponse(UserGoal goal) {
        return new UserGoalResponse(
                goal.getId(),
                goal.getDailyCalorieGoal(),
                goal.getProteinGoalGrams(),
                goal.getCarbsGoalGrams(),
                goal.getFatGoalGrams(),
                goal.getStartingWeightKg(),
                goal.getTargetWeightKg(),
                Boolean.TRUE.equals(goal.getActive()),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }

    public List<UserGoalResponse> toResponseList(List<UserGoal> goals) {
        return goals.stream().map(this::toResponse).toList();
    }
}
