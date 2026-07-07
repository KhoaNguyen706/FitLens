package com.example.fitlens.service.impl;

import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.enums.AuthProvider;
import com.example.fitlens.dto.request.LoginRequest;
import com.example.fitlens.dto.request.LogoutRequest;
import com.example.fitlens.dto.request.OAuthLoginRequest;
import com.example.fitlens.dto.request.RefreshTokenRequest;
import com.example.fitlens.dto.request.RegisterRequest;
import com.example.fitlens.dto.response.AuthResponse;
import com.example.fitlens.mapper.UserMapper;
import com.example.fitlens.repository.UserRepository;
import com.example.fitlens.security.JwtService;
import com.example.fitlens.security.oauth.AppleOAuthTokenVerifier;
import com.example.fitlens.security.oauth.GoogleOAuthTokenVerifier;
import com.example.fitlens.security.oauth.OAuthIdentity;
import com.example.fitlens.security.oauth.OAuthTokenVerifier;
import com.example.fitlens.service.AuthService;
import com.example.fitlens.service.RefreshTokenService;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final GoogleOAuthTokenVerifier googleOAuthTokenVerifier;
    private final AppleOAuthTokenVerifier appleOAuthTokenVerifier;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = userService.register(
                request.email(),
                request.password(),
                request.displayName()
        );

        return buildAuthResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getPasswordHash() == null) {
            throw new BadCredentialsException(
                    "This account uses Google or Apple sign-in. Use that method instead."
            );
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(OAuthLoginRequest request) {
        return loginWithOAuth(AuthProvider.GOOGLE, googleOAuthTokenVerifier, request);
    }

    @Override
    @Transactional
    public AuthResponse loginWithApple(OAuthLoginRequest request) {
        return loginWithOAuth(AuthProvider.APPLE, appleOAuthTokenVerifier, request);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        User user = refreshTokenService.validateAndRotate(request.refreshToken());
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }

    private AuthResponse loginWithOAuth(
            AuthProvider provider,
            OAuthTokenVerifier verifier,
            OAuthLoginRequest request
    ) {
        OAuthIdentity identity = verifier.verify(request.idToken());
        User user = userService.findOrCreateOAuthUser(provider, identity, request.displayName());
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponse(token, refreshToken, userMapper.toResponse(user));
    }
}
