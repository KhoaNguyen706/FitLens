package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.DailyDashboardResponse;
import com.example.fitlens.security.AuthUser;
import com.example.fitlens.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ApiResponseFactory responses;

    @GetMapping("/today")
    public ApiResponse<DailyDashboardResponse> getTodayDashboard(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day
    ) {
        return responses.ok(dashboardService.getTodayDashboard(authUser.id(), day));
    }
}
