package com.brijesh.vehicle_manager.dto;

import com.brijesh.vehicle_manager.entity.FuelType;
import com.brijesh.vehicle_manager.entity.VehicleType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Payload for updating vehicle.
 * All fields optional except validation constraints when present.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleUpdateRequest {

    @Size(max = 100)
    private String nickname;

    @Size(max = 50)
    private String registrationNumber;

    @Size(max = 100)
    private String make;

    @Size(max = 100)
    private String model;

    @Min(1900)
    @Max(2100)
    private Integer yearOfManufacture;

    private VehicleType vehicleType;

    private FuelType fuelType;

    private LocalDate purchaseDate;

    @Min(0)
    private Long currentOdometer;
}
