package com.brijesh.vehicle_manager.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Response returned on successful auth/login/refresh.
 */
@Getter
@Setter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn; // seconds

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

}
