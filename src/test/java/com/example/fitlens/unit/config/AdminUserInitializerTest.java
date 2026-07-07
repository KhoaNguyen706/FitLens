package com.example.fitlens.unit.config;

import com.example.fitlens.config.AdminUserInitializer;
import com.example.fitlens.config.AdminUserProperties;
import com.example.fitlens.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserInitializerTest {

    @Mock
    private UserService userService;

    @Mock
    private AdminUserProperties adminUserProperties;

    @InjectMocks
    private AdminUserInitializer adminUserInitializer;

    @Test
    void run_skipsWhenAdminAlreadyExists() throws Exception {
        when(adminUserProperties.email()).thenReturn("admin@fitlens.local");
        when(adminUserProperties.password()).thenReturn("admin123456");
        when(adminUserProperties.displayName()).thenReturn("Admin");
        when(userService.existsByEmail("admin@fitlens.local")).thenReturn(true);

        adminUserInitializer.run(new DefaultApplicationArguments(new String[0]));

        verify(userService, never()).register(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void run_createsAdminWhenMissing() throws Exception {
        when(adminUserProperties.email()).thenReturn("admin@fitlens.local");
        when(adminUserProperties.password()).thenReturn("admin123456");
        when(adminUserProperties.displayName()).thenReturn("Admin");
        when(userService.existsByEmail("admin@fitlens.local")).thenReturn(false);

        adminUserInitializer.run(new DefaultApplicationArguments(new String[0]));

        verify(userService).register("admin@fitlens.local", "admin123456", "Admin");
    }
}
