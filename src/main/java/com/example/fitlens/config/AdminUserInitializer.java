package com.example.fitlens.config;

import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "fitlens.admin.enabled", havingValue = "true", matchIfMissing = true)
public class AdminUserInitializer implements ApplicationRunner {

    private final UserService userService;
    private final AdminUserProperties adminUserProperties;

    @Override
    public void run(ApplicationArguments args) {
        if (!isConfigured()) {
            log.warn("Admin user seeding skipped: fitlens.admin.email and fitlens.admin.password must be set");
            return;
        }

        if (userService.existsByEmail(adminUserProperties.email())) {
            log.info("Admin user already exists ({}). Skipping seed.", adminUserProperties.email());
            return;
        }

        userService.register(
                adminUserProperties.email(),
                adminUserProperties.password(),
                adminUserProperties.displayName()
        );

        log.info("Default admin user created: {}", adminUserProperties.email());
    }

    private boolean isConfigured() {
        return StringUtils.hasText(adminUserProperties.email())
                && StringUtils.hasText(adminUserProperties.password())
                && StringUtils.hasText(adminUserProperties.displayName());
    }
}
