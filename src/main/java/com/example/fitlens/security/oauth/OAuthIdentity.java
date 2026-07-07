package com.example.fitlens.security.oauth;

public record OAuthIdentity(
        String subject,
        String email,
        String displayName,
        boolean emailVerified
) {
}
