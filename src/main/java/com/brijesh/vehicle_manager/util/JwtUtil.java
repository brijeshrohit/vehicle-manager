package com.brijesh.vehicle_manager.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Utility wrapper around JJWT to create and parse JWT tokens.
 * <p>
 * - Access and refresh tokens are both JWTs here for simplicity, but refresh tokens are stored in DB to enable rotation/revocation.
 * - Secret should be long and kept in environment variables in production.
 */
@Component
public class JwtUtil {

    private final SecretKey key;

//    private final Key key;
    private final long accessTokenValiditySeconds;
    private final long refreshTokenValiditySeconds;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.access-validity-sec:3600}") long accessTokenValiditySeconds,
                   @Value("${app.jwt.refresh-validity-sec:1209600}") long refreshTokenValiditySeconds) {
        // secret should be base64-encoded; if plain text, we can decode or use getBytes() (but prefer long base64)
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    /**
     * Generate an access token for the given userId.
     * Subject is userId string (UUID).
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
     * Optionally generate a refresh token (JWT form) — although refresh tokens will also be stored server-side.
     * We keep refresh token as JWT for traceability, but server will verify existence in DB before accepting.
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
     * Parse token subject (userId). Throws runtime JWT exceptions on invalid token.
     */
    public UUID parseTokenSubject(String token) {
        var claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Returns token expiration as Instant
     */
    public Instant getExpiration(String token) {
        var claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getExpiration().toInstant();
    }
}
