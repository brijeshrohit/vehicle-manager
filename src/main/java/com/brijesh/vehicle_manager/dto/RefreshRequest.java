package com.brijesh.vehicle_manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Refresh token request body.
 */
@Getter
@Setter
public class RefreshRequest {
    @NotBlank
    private String refreshToken;
}
