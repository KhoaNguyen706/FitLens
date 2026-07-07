package com.example.fitlens.repository;

import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByAuthProviderAndOauthSubject(AuthProvider authProvider, String oauthSubject);
}
