package com.example.fitlens.unit.service;

import com.example.fitlens.domain.entity.User;
import com.example.fitlens.exception.DuplicateEmailException;
import com.example.fitlens.repository.UserRepository;
import com.example.fitlens.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_createsUserWhenEmailIsAvailable() {
        when(userRepository.existsByEmail("khoa@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("khoa@example.com");
        savedUser.setDisplayName("Khoa");
        savedUser.setPasswordHash("hashed-password");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register("khoa@example.com", "password123", "Khoa");

        assertThat(result.getEmail()).isEqualTo("khoa@example.com");
        assertThat(result.getDisplayName()).isEqualTo("Khoa");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("hashed-password");
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("khoa@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("khoa@example.com", "password123", "Khoa"))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any(User.class));
    }
}
