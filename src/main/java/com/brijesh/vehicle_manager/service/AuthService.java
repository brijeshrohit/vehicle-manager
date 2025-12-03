package com.brijesh.vehicle_manager.service;

import com.brijesh.vehicle_manager.dto.AuthResponse;
import com.brijesh.vehicle_manager.dto.AuthRequest;
import com.brijesh.vehicle_manager.dto.RegisterRequest;
import com.brijesh.vehicle_manager.entity.RefreshToken;
import com.brijesh.vehicle_manager.entity.User;
import com.brijesh.vehicle_manager.repository.RefreshTokenRepository;
import com.brijesh.vehicle_manager.repository.UserRepository;
import com.brijesh.vehicle_manager.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Authentication service:
 * - register: create user, hash password, issue tokens and persist refresh token
 * - login: validate password, issue tokens, persist refresh token
 * - refresh: validate refresh token (exists in DB and not expired) -> rotate and issue new tokens
 *
 * The refresh tokens are rotated: on successful refresh we create a new refresh token record and delete (or replace) the old one,
 * preventing reuse of stolen tokens after rotation.
 */
@Service
public class AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepo,
                       RefreshTokenRepository refreshRepo,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.refreshRepo = refreshRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        // Validate uniqueness
        userRepo.findByEmail(req.getEmail().toLowerCase()).ifPresent(u -> {
            throw new RuntimeException("Email already registered");
        });

        // create user
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setEmail(req.getEmail().toLowerCase());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setDisplayName(req.getDisplayName());
        userRepo.save(u);

        // create tokens
        String access = jwtUtil.generateAccessToken(u.getId());
        String refreshToken = jwtUtil.generateRefreshToken(u.getId());

        // Persist refresh token server-side with expiry
        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setUserId(u.getId());
        rt.setExpiresAt(jwtUtil.getExpiration(refreshToken));
        refreshRepo.deleteByUserId(u.getId()); // in case previous tokens exist
        refreshRepo.save(rt);

        return new AuthResponse(access, refreshToken, Long.valueOf( (long) (jwtUtil.getExpiration(access).getEpochSecond() - Instant.now().getEpochSecond()) ));
    }

    @Transactional
    public AuthResponse login(AuthRequest req) {
        User u = userRepo.findByEmail(req.getEmail().toLowerCase()).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        String access = jwtUtil.generateAccessToken(u.getId());
        String refreshToken = jwtUtil.generateRefreshToken(u.getId());

        // rotate: delete old and save new
        refreshRepo.deleteByUserId(u.getId());
        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setUserId(u.getId());
        rt.setExpiresAt(jwtUtil.getExpiration(refreshToken));
        refreshRepo.save(rt);

        return new AuthResponse(access, refreshToken, Long.valueOf( (long) (jwtUtil.getExpiration(access).getEpochSecond() - Instant.now().getEpochSecond()) ));
    }

    @Transactional
    public AuthResponse refresh(String oldRefreshToken) {
        RefreshToken stored = refreshRepo.findByToken(oldRefreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshRepo.delete(stored);
            throw new RuntimeException("Refresh token expired");
        }

        UUID userId = stored.getUserId();
        // rotate refresh token: delete old and issue new
        refreshRepo.delete(stored);

        String newRefreshToken = jwtUtil.generateRefreshToken(userId);
        RefreshToken newRt = new RefreshToken();
        newRt.setToken(newRefreshToken);
        newRt.setUserId(userId);
        newRt.setExpiresAt(jwtUtil.getExpiration(newRefreshToken));
        refreshRepo.save(newRt);

        String access = jwtUtil.generateAccessToken(userId);

        return new AuthResponse(access, newRefreshToken, Long.valueOf( (long) (jwtUtil.getExpiration(access).getEpochSecond() - Instant.now().getEpochSecond()) ));
    }

    /**
     * Logout: remove refresh tokens for the user (optional endpoint).
     */
    @Transactional
    public void logout(UUID userId) {
        refreshRepo.deleteByUserId(userId);
    }
}
