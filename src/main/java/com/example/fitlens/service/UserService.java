package com.example.fitlens.service;

import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.enums.AuthProvider;
import com.example.fitlens.security.oauth.OAuthIdentity;

public interface UserService {

    User getById(Long userId);

    User getByEmail(String email);

    boolean existsByEmail(String email);

    User register(String email, String rawPassword, String displayName);

    User findOrCreateOAuthUser(AuthProvider provider, OAuthIdentity identity, String fallbackDisplayName);
}
