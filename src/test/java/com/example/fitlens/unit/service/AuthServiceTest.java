package com.example.fitlens.unit.service;

import com.example.fitlens.domain.entity.User;
import com.example.fitlens.dto.request.LoginRequest;
import com.example.fitlens.dto.response.UserResponse;
import com.example.fitlens.mapper.UserMapper;
import com.example.fitlens.repository.UserRepository;
import com.example.fitlens.security.JwtService;
import com.example.fitlens.service.RefreshTokenService;
import com.example.fitlens.service.UserService;
import com.example.fitlens.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_returnsTokenWhenCredentialsAreValid() {
        User user = new User();
        user.setId(1L);
        user.setEmail("khoa@example.com");
        user.setPasswordHash("hashed-password");

        when(userRepository.findByEmail("khoa@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(1L, "khoa@example.com")).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn("refresh-token");
        when(userMapper.toResponse(user)).thenReturn(
                new UserResponse(1L, "khoa@example.com", "Khoa", Instant.parse("2026-07-01T00:00:00Z"))
        );

        var response = authService.login(new LoginRequest("khoa@example.com", "password123"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.user().email()).isEqualTo("khoa@example.com");
    }

    @Test
    void login_throwsWhenPasswordIsInvalid() {
        User user = new User();
        user.setEmail("khoa@example.com");
        user.setPasswordHash("hashed-password");

        when(userRepository.findByEmail("khoa@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("khoa@example.com", "wrong-password")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }
}
