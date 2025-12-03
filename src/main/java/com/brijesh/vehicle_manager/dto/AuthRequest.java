package com.brijesh.vehicle_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Login payload.
 */
@Getter
@Setter
public class AuthRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

}
