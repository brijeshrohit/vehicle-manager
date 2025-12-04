package com.brijesh.vehicle_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Vehicle entity.
 * Soft-delete is supported via `deleted` flag.
 */
@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_vehicle_owner", columnList = "ownerId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    private UUID id;

    // Owner (user) id - maps to User.id
    @Column(nullable = false)
    private UUID ownerId;

    @Column(length = 100)
    private String nickname;

    @Column(length = 50)
    private String registrationNumber;

    @Column(length = 100)
    private String make;

    @Column(length = 100)
    private String model;

    private Integer yearOfManufacture;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private LocalDate purchaseDate;

    // Odometer values in kilometers
    private Long initialOdometer;

    private Long currentOdometer;

    private boolean deleted = false;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}
