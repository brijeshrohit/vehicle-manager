package com.brijesh.vehicle_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Fuel fill record. All monetary/volume fields use BigDecimal for precision.
 */
@Entity
@Table(name = "fuel_fills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelFill {

    @Id
    private UUID id;

    // Owner of the vehicle (user id)
    @Column(nullable = false)
    private UUID userId;

    // Vehicle id (we don't map to Vehicle entity object; we use ownerId check separately)
    @Column(nullable = false)
    private UUID vehicleId;

    @Column(nullable = false)
    private LocalDate fillDate;

    // Odometer reading captured at fill time (km)
    @Column(nullable = false)
    private Long odometerReading;

    // Volume in liters (nullable if system estimates)
    @Column(precision = 12, scale = 3)
    private BigDecimal fuelVolume;

    // Price per liter (nullable if system estimates)
    @Column(precision = 12, scale = 2)
    private BigDecimal pricePerLiter;

    // Always computed server-side = fuelVolume * pricePerLiter
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    // Mileage calculated for this fill (km per litre), nullable if cannot compute
    @Column(precision = 8, scale = 3)
    private BigDecimal mileage;

    // true if one or more fields were estimated
    @Column(nullable = false)
    private boolean estimated;

    private String notes;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}
