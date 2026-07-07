package com.example.fitlens.service;

import com.example.fitlens.domain.entity.WeightLog;
import com.example.fitlens.dto.request.CreateWeightLogRequest;

import java.util.List;

public interface WeightLogService {

    List<WeightLog> getWeightHistory(Long userId);

    WeightLog getLatestWeight(Long userId);

    WeightLog logWeight(Long userId, CreateWeightLogRequest request);

    void deleteWeightLog(Long userId, Long weightLogId);
}
