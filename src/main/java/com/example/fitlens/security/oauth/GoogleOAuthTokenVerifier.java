package com.example.fitlens.security.oauth;

import com.example.fitlens.config.OAuthProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Component
public class GoogleOAuthTokenVerifier implements OAuthTokenVerifier {

    private static final String TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo";

    private final RestClient restClient;
    private final List<String> allowedClientIds;

    public GoogleOAuthTokenVerifier(OAuthProperties oauthProperties) {
        this.restClient = RestClient.create();
        this.allowedClientIds = oauthProperties.google().clientIds();
    }

    @Override
    @SuppressWarnings("unchecked")
    public OAuthIdentity verify(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("Google ID token is required");
        }

        Map<String, Object> payload;
        try {
            payload = restClient.get()
                    .uri(TOKEN_INFO_URL + "?id_token={token}", idToken)
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientException ex) {
            throw new IllegalArgumentException("Invalid Google ID token");
        }

        if (payload == null || payload.containsKey("error_description")) {
            throw new IllegalArgumentException("Invalid Google ID token");
        }

        String audience = stringValue(payload.get("aud"));
        if (allowedClientIds.isEmpty() || !allowedClientIds.contains(audience)) {
            throw new IllegalArgumentException("Google token audience is not allowed");
        }

        String email = stringValue(payload.get("email"));
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google account email is missing");
        }

        if (!"true".equalsIgnoreCase(stringValue(payload.get("email_verified")))) {
            throw new IllegalArgumentException("Google account email is not verified");
        }

        String subject = stringValue(payload.get("sub"));
        String name = stringValue(payload.get("name"));
        if (name == null || name.isBlank()) {
            name = email.substring(0, email.indexOf('@'));
        }

        return new OAuthIdentity(subject, email.toLowerCase(), name, true);
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
