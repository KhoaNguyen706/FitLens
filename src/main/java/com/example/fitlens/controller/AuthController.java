package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.dto.request.LoginRequest;
import com.example.fitlens.dto.request.LogoutRequest;
import com.example.fitlens.dto.request.OAuthLoginRequest;
import com.example.fitlens.dto.request.RefreshTokenRequest;
import com.example.fitlens.dto.request.RegisterRequest;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.AuthResponse;
import com.example.fitlens.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ApiResponseFactory responses;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return responses.ok("Registration successful", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return responses.ok("Login successful", authService.login(request));
    }

    @PostMapping("/oauth/google")
    public ApiResponse<AuthResponse> loginWithGoogle(@Valid @RequestBody OAuthLoginRequest request) {
        return responses.ok("Google sign-in successful", authService.loginWithGoogle(request));
    }

    @PostMapping("/oauth/apple")
    public ApiResponse<AuthResponse> loginWithApple(@Valid @RequestBody OAuthLoginRequest request) {
        return responses.ok("Apple sign-in successful", authService.loginWithApple(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return responses.ok("Token refreshed", authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return responses.message("Logged out");
    }
}
