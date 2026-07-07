package com.example.fitlens.common.web;

import com.example.fitlens.dto.response.ApiResponse;
import org.springframework.stereotype.Component;

@Component
public class ApiResponseFactory {

    public <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }

    public <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    public ApiResponse<Void> message(String message) {
        return ApiResponse.<Void>builder()
                .message(message)
                .build();
    }
}
