package com.brijesh.vehicle_manager.service;

import com.brijesh.vehicle_manager.dto.AuthRequest;
import com.brijesh.vehicle_manager.dto.AuthResponse;
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
 * - register: create user -> issue tokens and persist refresh token
 * - login: validate password -> issue tokens and persist refresh token (rotating)
 * - refresh: validate provided refresh token exists and not expired -> rotate (delete old, store new) and issue new access token
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
        // ensure unique email
        userRepo.findByEmail(req.getEmail().toLowerCase()).ifPresent(u -> {
            throw new IllegalStateException("Email already registered");
        });

        User u = new User();
        u.setId(UUID.randomUUID());
        u.setEmail(req.getEmail().toLowerCase());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setDisplayName(req.getDisplayName());
        userRepo.save(u);

        String access = jwtUtil.generateAccessToken(u.getId());
        String refresh = jwtUtil.generateRefreshToken(u.getId());

        // Persist refresh token for rotation/revocation
        RefreshToken rt = new RefreshToken();
        rt.setToken(refresh);
        rt.setUserId(u.getId());
        rt.setExpiresAt(jwtUtil.getExpiration(refresh));
        refreshRepo.deleteByUserId(u.getId()); // clean existing
        refreshRepo.save(rt);

        long expiresIn = jwtUtil.getExpiration(access).getEpochSecond() - Instant.now().getEpochSecond();
        return new AuthResponse(access, refresh, expiresIn);
    }

    @Transactional
    public AuthResponse login(AuthRequest req) {
        User u = userRepo.findByEmail(req.getEmail().toLowerCase()).orElseThrow(() -> new IllegalStateException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new IllegalStateException("Invalid credentials");
        }

        String access = jwtUtil.generateAccessToken(u.getId());
        String refresh = jwtUtil.generateRefreshToken(u.getId());

        // rotate refresh token: delete old and save new
        refreshRepo.deleteByUserId(u.getId());
        RefreshToken rt = new RefreshToken();
        rt.setToken(refresh);
        rt.setUserId(u.getId());
        rt.setExpiresAt(jwtUtil.getExpiration(refresh));
        refreshRepo.save(rt);

        long expiresIn = jwtUtil.getExpiration(access).getEpochSecond() - Instant.now().getEpochSecond();
        return new AuthResponse(access, refresh, expiresIn);
    }

    @Transactional
    public AuthResponse refresh(String oldRefreshToken) {
        RefreshToken stored = refreshRepo.findByToken(oldRefreshToken).orElseThrow(() -> new IllegalStateException("Invalid refresh token"));
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshRepo.delete(stored);
            throw new IllegalStateException("Refresh token expired");
        }

        UUID userId = stored.getUserId();
        // rotate
        refreshRepo.delete(stored);

        String newRefresh = jwtUtil.generateRefreshToken(userId);
        RefreshToken rt = new RefreshToken();
        rt.setToken(newRefresh);
        rt.setUserId(userId);
        rt.setExpiresAt(jwtUtil.getExpiration(newRefresh));
        refreshRepo.save(rt);

        String access = jwtUtil.generateAccessToken(userId);
        long expiresIn = jwtUtil.getExpiration(access).getEpochSecond() - Instant.now().getEpochSecond();
        return new AuthResponse(access, newRefresh, expiresIn);
    }

    @Transactional
    public void logout(UUID userId) {
        refreshRepo.deleteByUserId(userId);
    }
}
