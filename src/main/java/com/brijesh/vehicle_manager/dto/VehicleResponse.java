package com.brijesh.vehicle_manager.dto;

import com.brijesh.vehicle_manager.entity.FuelType;
import com.brijesh.vehicle_manager.entity.VehicleType;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response object returned to clients.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    private UUID id;
    private UUID ownerId;
    private String nickname;
    private String registrationNumber;
    private String make;
    private String model;
    private Integer yearOfManufacture;
    private VehicleType vehicleType;
    private FuelType fuelType;
    private LocalDate purchaseDate;
    private Long initialOdometer;
    private Long currentOdometer;
    private boolean deleted;
    private Instant createdAt;
    private Instant updatedAt;
}
