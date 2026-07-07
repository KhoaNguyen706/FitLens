package com.example.fitlens.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fitlens.ai")
public record AiProperties(
        boolean enabled,
        String url,
        int timeoutSeconds
) {

    public AiProperties {
        url = url == null || url.isBlank() ? "http://localhost:8000" : url;
        timeoutSeconds = timeoutSeconds <= 0 ? 35 : timeoutSeconds;
    }
}
