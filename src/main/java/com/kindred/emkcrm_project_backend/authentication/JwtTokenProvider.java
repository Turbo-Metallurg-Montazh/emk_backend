package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm_project_backend.config.ActivationProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {

    private final ActivationProperties activationProperties;

    public JwtTokenProvider(
            ActivationProperties activationProperties
    ) {
        this.activationProperties = activationProperties;
    }

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds;

    private static SecretKey key;

    private static SecretKey activationKey;


    @PostConstruct
    protected void init() {
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("The secret key must be at least 32 characters long");
        }
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
        activationKey = Keys.hmacShaKeyFor(activationProperties.key().getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .claim("sub", username)
                .claim("iat", new Date())  // Issued at
                .claim("exp", new Date(System.currentTimeMillis() + validityInMilliseconds))  // Expiration
                .signWith(key)// Sign with the SecretKey
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateActivateToken(String email) {
        return Jwts.builder()
                .claim("sub", email)
                .claim("iat", new Date())  // Issued at
                .claim("exp", new Date(System.currentTimeMillis() + activationProperties.expiration_ms()))  // Expiration
                .signWith(activationKey)  // Sign with the SecretKey
                .compact();
    }

    public void validateActivationToken(String token) {
        Jwts.parser().verifyWith(activationKey).build().parseSignedClaims(token);
    }

    public String getEmailFromActivationToken(String token) {
        return Jwts.parser().verifyWith(activationKey).build().parseSignedClaims(token).getPayload().getSubject();
    }
}