package com.example.fitlens.service;

import com.example.fitlens.dto.response.DailyDashboardResponse;

import java.time.LocalDate;

public interface DashboardService {

    DailyDashboardResponse getTodayDashboard(Long userId, LocalDate day);
}
