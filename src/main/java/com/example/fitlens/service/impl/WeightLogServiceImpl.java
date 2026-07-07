package com.example.fitlens.service.impl;

import com.example.fitlens.common.security.OwnedResourceValidator;
import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.entity.WeightLog;
import com.example.fitlens.dto.request.CreateWeightLogRequest;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.WeightLogRepository;
import com.example.fitlens.service.UserService;
import com.example.fitlens.service.WeightLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeightLogServiceImpl implements WeightLogService {

    private final UserService userService;
    private final WeightLogRepository weightLogRepository;
    private final OwnedResourceValidator ownedResourceValidator;

    @Override
    @Transactional(readOnly = true)
    public List<WeightLog> getWeightHistory(Long userId) {
        userService.getById(userId);
        return weightLogRepository.findByUserIdOrderByLoggedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public WeightLog getLatestWeight(Long userId) {
        userService.getById(userId);

        return weightLogRepository.findFirstByUserIdOrderByLoggedAtDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Weight log not found for user: " + userId));
    }

    @Override
    @Transactional
    public WeightLog logWeight(Long userId, CreateWeightLogRequest request) {
        User user = userService.getById(userId);

        WeightLog weightLog = new WeightLog();
        weightLog.setUser(user);
        weightLog.setWeightKg(request.weightKg());
        weightLog.setLoggedAt(request.loggedAt() != null ? request.loggedAt() : Instant.now());
        weightLog.setNotes(request.notes());

        return weightLogRepository.save(weightLog);
    }

    @Override
    @Transactional
    public void deleteWeightLog(Long userId, Long weightLogId) {
        WeightLog weightLog = weightLogRepository.findById(weightLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Weight log not found: " + weightLogId));

        ownedResourceValidator.assertSameOwner(weightLog.getUser().getId(), userId, "Weight log", weightLogId);
        weightLogRepository.delete(weightLog);
    }
}
