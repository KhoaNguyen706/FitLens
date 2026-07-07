package com.example.fitlens.service;

import com.example.fitlens.dto.request.LoginRequest;
import com.example.fitlens.dto.request.LogoutRequest;
import com.example.fitlens.dto.request.OAuthLoginRequest;
import com.example.fitlens.dto.request.RefreshTokenRequest;
import com.example.fitlens.dto.request.RegisterRequest;
import com.example.fitlens.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse loginWithGoogle(OAuthLoginRequest request);

    AuthResponse loginWithApple(OAuthLoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}
