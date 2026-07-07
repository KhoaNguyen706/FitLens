package com.example.fitlens.security.oauth;

import com.example.fitlens.config.OAuthProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Component
public class AppleOAuthTokenVerifier implements OAuthTokenVerifier {

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final String allowedClientId;
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public AppleOAuthTokenVerifier(OAuthProperties oauthProperties) throws Exception {
        this.allowedClientId = oauthProperties.apple().clientId();

        JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(APPLE_JWKS_URL));
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);

        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(keySelector);
        this.jwtProcessor = processor;
    }

    @Override
    public OAuthIdentity verify(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("Apple ID token is required");
        }
        if (allowedClientId == null || allowedClientId.isBlank()) {
            throw new IllegalArgumentException("Apple OAuth is not configured on the server");
        }

        try {
            SignedJWT signedJwt = SignedJWT.parse(idToken);
            JWTClaimsSet claims = jwtProcessor.process(signedJwt, null);

            if (!APPLE_ISSUER.equals(claims.getIssuer())) {
                throw new IllegalArgumentException("Invalid Apple token issuer");
            }

            String audience = claims.getAudience() == null || claims.getAudience().isEmpty()
                    ? null
                    : claims.getAudience().getFirst();
            if (!allowedClientId.equals(audience)) {
                throw new IllegalArgumentException("Apple token audience is not allowed");
            }

            Date expiration = claims.getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                throw new IllegalArgumentException("Apple ID token has expired");
            }

            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                throw new IllegalArgumentException("Apple account subject is missing");
            }

            String email = claims.getStringClaim("email");
            boolean emailVerified = Boolean.TRUE.equals(claims.getBooleanClaim("email_verified"));

            if (email != null && !email.isBlank()) {
                email = email.toLowerCase();
            }

            return new OAuthIdentity(subject, email, null, emailVerified);
        } catch (ParseException | JOSEException | com.nimbusds.jose.proc.BadJOSEException ex) {
            throw new IllegalArgumentException("Invalid Apple ID token");
        }
    }
}
