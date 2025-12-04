package com.brijesh.vehicle_manager.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

/**
 * Client may omit fuelVolume or pricePerLiter to request server estimation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelFillCreateRequest {
    @NotNull
    private UUID vehicleId;
    @NotNull
    private LocalDate fillDate;
    @NotNull
    private Long odometerReading;

    // optional — server will estimate if null and history allows it
    private BigDecimal fuelVolume;
    private BigDecimal pricePerLiter;

    private String notes;
}


