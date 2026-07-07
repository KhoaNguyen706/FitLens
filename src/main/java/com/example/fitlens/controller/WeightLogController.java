package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.dto.request.CreateWeightLogRequest;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.WeightLogResponse;
import com.example.fitlens.mapper.WeightLogMapper;
import com.example.fitlens.security.AuthUser;
import com.example.fitlens.service.WeightLogService;
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
@RequestMapping("/api/weight-logs")
@RequiredArgsConstructor
public class WeightLogController {

    private final WeightLogService weightLogService;
    private final WeightLogMapper weightLogMapper;
    private final ApiResponseFactory responses;

    @GetMapping
    public ApiResponse<List<WeightLogResponse>> getWeightHistory(@AuthenticationPrincipal AuthUser authUser) {
        return responses.ok(weightLogMapper.toResponseList(
                weightLogService.getWeightHistory(authUser.id())
        ));
    }

    @GetMapping("/latest")
    public ApiResponse<WeightLogResponse> getLatestWeight(@AuthenticationPrincipal AuthUser authUser) {
        return responses.ok(weightLogMapper.toResponse(
                weightLogService.getLatestWeight(authUser.id())
        ));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WeightLogResponse> logWeight(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateWeightLogRequest request
    ) {
        return responses.ok(
                "Weight logged successfully",
                weightLogMapper.toResponse(weightLogService.logWeight(authUser.id(), request))
        );
    }

    @DeleteMapping("/{weightLogId}")
    public ApiResponse<Void> deleteWeightLog(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long weightLogId
    ) {
        weightLogService.deleteWeightLog(authUser.id(), weightLogId);
        return responses.message("Weight log deleted successfully");
    }
}
