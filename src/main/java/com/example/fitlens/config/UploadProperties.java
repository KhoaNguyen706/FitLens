package com.example.fitlens.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fitlens.uploads")
public record UploadProperties(
        String dir
) {
}
