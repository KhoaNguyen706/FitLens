package com.example.fitlens.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "fitlens.oauth")
public record OAuthProperties(
        Google google,
        Apple apple
) {

    public OAuthProperties {
        google = google != null ? google : new Google(new ArrayList<>());
        apple = apple != null ? apple : new Apple("");
    }

    public record Google(List<String> clientIds) {
    }

    public record Apple(String clientId) {
    }
}
