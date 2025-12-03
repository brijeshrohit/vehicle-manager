package com.brijesh.vehicle_manager.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * JWT helper to generate and parse tokens.
 * - Uses HS256 (HMAC) with a secret key configured in application.yml
 * - For dev you can use a plain string; in prod use a long base64 secret stored as env variable.
 */
@Component
public class JwtUtil {

    private final String secret;
    private final long accessTokenValiditySeconds;
    private final long refreshTokenValiditySeconds;

    private Key key;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-validity-sec:3600}") long accessTokenValiditySeconds,
            @Value("${app.jwt.refresh-validity-sec:1209600}") long refreshTokenValiditySeconds
    ) {
        this.secret = secret;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    @PostConstruct
    public void init() {
        // Accept either plain secret or base64 encoded. If it's base64-like (contains '=' or '/'), try decode;
        // otherwise use raw bytes. In production use long base64 secret.
        byte[] keyBytes;
        try {
            // First try base64 decode - if fails, fallback to raw bytes
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception ex) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Create access token (short lived) with subject = userId (UUID string)
     */
    public String generateAccessToken(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTokenValiditySeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Create refresh token (longer lived). We still store it in DB for revocation/rotation.
     */
    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userId.toString())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTokenValiditySeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parse subject (userId) from a token. Throws JwtException when invalid/expired.
     */
    public UUID parseTokenSubject(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Return token expiration time as Instant
     */
    public Instant getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().toInstant();
    }
}
