package com.brijesh.vehicle_manager.service;

import com.brijesh.vehicle_manager.dto.AuthRequest;
import com.brijesh.vehicle_manager.dto.AuthResponse;
import com.brijesh.vehicle_manager.dto.RegisterRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AuthService {
    @Transactional
    AuthResponse register(RegisterRequest req);

    @Transactional
    AuthResponse login(AuthRequest req);

    @Transactional
    AuthResponse refresh(String oldRefreshToken);

    @Transactional
    void logout(UUID userId);
}
