package com.example.fitlens.service;

import com.example.fitlens.domain.entity.User;

public interface RefreshTokenService {

    String createRefreshToken(User user);

    User validateAndRotate(String rawRefreshToken);

    void revoke(String rawRefreshToken);
}
