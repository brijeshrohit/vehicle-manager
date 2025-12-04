package com.brijesh.vehicle_manager.dto;

import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Client may omit fuelVolume or pricePerLiter to request server estimation.
 */
@Data
public class FuelFillCreateRequest {

    @NotNull
    private UUID vehicleId;

    @NotNull
    private LocalDate fillDate;

    @NotNull
    private Long odometer;

    @NotNull
    private Double liters;

    @NotNull
    private Double pricePerLiter;

    private Boolean fullTank;

    private Boolean estimated;
}


