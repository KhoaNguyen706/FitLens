package com.example.fitlens.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fitlens.admin")
public record AdminUserProperties(
        boolean enabled,
        String email,
        String password,
        String displayName
) {
}
