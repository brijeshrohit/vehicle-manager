package com.brijesh.vehicle_manager.controller;

import com.brijesh.vehicle_manager.dto.AuthRequest;
import com.brijesh.vehicle_manager.dto.AuthResponse;
import com.brijesh.vehicle_manager.dto.RegisterRequest;
import com.brijesh.vehicle_manager.dto.RefreshRequest;
import com.brijesh.vehicle_manager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * Auth REST endpoints
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse resp = authService.register(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse resp = authService.login(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        AuthResponse resp = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String,String>> logout(Principal principal) {
        if (principal == null) return ResponseEntity.badRequest().body(Map.of("status","no-token"));
        UUID userId = UUID.fromString(principal.getName());
        authService.logout(userId);
        return ResponseEntity.ok(Map.of("status","User successfully logged out"));
    }
}
