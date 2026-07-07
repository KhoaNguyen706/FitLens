package com.example.fitlens.service.impl;

import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.enums.AuthProvider;
import com.example.fitlens.exception.DuplicateEmailException;
import com.example.fitlens.exception.OAuthAccountConflictException;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.UserRepository;
import com.example.fitlens.security.oauth.OAuthIdentity;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User register(String email, String rawPassword, String displayName) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setDisplayName(displayName);
        user.setAuthProvider(AuthProvider.LOCAL);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User findOrCreateOAuthUser(
            AuthProvider provider,
            OAuthIdentity identity,
            String fallbackDisplayName
    ) {
        return userRepository.findByAuthProviderAndOauthSubject(provider, identity.subject())
                .orElseGet(() -> resolveOrCreateOAuthUser(provider, identity, fallbackDisplayName));
    }

    private User resolveOrCreateOAuthUser(
            AuthProvider provider,
            OAuthIdentity identity,
            String fallbackDisplayName
    ) {
        if (identity.email() != null && !identity.email().isBlank()) {
            var existingByEmail = userRepository.findByEmail(identity.email());
            if (existingByEmail.isPresent()) {
                User existing = existingByEmail.get();
                if (existing.getAuthProvider() == AuthProvider.LOCAL) {
                    throw new OAuthAccountConflictException(
                            "This email is already registered with email/password. Log in with your password instead."
                    );
                }
                if (existing.getAuthProvider() != provider) {
                    throw new OAuthAccountConflictException(
                            "This email is already linked to a different sign-in provider."
                    );
                }
                return existing;
            }
        }

        User user = new User();
        user.setEmail(requireEmail(provider, identity));
        user.setAuthProvider(provider);
        user.setOauthSubject(identity.subject());
        user.setPasswordHash(null);
        user.setDisplayName(resolveDisplayName(identity, fallbackDisplayName, user.getEmail()));

        return userRepository.save(user);
    }

    private String requireEmail(AuthProvider provider, OAuthIdentity identity) {
        if (identity.email() == null || identity.email().isBlank()) {
            throw new OAuthAccountConflictException(
                    "No email returned for " + provider.name() + " sign-in. Use email/password or try again."
            );
        }
        return identity.email();
    }

    private String resolveDisplayName(OAuthIdentity identity, String fallbackDisplayName, String email) {
        if (identity.displayName() != null && !identity.displayName().isBlank()) {
            return identity.displayName();
        }
        if (fallbackDisplayName != null && !fallbackDisplayName.isBlank()) {
            return fallbackDisplayName.trim();
        }
        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }
}
