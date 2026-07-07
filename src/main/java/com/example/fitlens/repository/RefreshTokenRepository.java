package com.example.fitlens.repository;

import com.example.fitlens.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("""
            SELECT rt FROM RefreshToken rt
            JOIN FETCH rt.user
            WHERE rt.tokenHash = :tokenHash
              AND rt.revokedAt IS NULL
              AND rt.expiresAt > CURRENT_TIMESTAMP
            """)
    Optional<RefreshToken> findActiveByTokenHash(@Param("tokenHash") String tokenHash);
}
