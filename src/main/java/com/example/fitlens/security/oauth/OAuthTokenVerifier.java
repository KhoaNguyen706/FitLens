package com.example.fitlens.security.oauth;

public interface OAuthTokenVerifier {

    OAuthIdentity verify(String idToken);
}
