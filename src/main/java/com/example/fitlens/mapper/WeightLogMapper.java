package com.example.fitlens.mapper;

import com.example.fitlens.domain.entity.WeightLog;
import com.example.fitlens.dto.response.WeightLogResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeightLogMapper {

    public WeightLogResponse toResponse(WeightLog weightLog) {
        return new WeightLogResponse(
                weightLog.getId(),
                weightLog.getWeightKg(),
                weightLog.getLoggedAt(),
                weightLog.getNotes(),
                weightLog.getCreatedAt()
        );
    }

    public List<WeightLogResponse> toResponseList(List<WeightLog> weightLogs) {
        return weightLogs.stream().map(this::toResponse).toList();
    }
}
