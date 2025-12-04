package com.brijesh.vehicle_manager.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelFillResponse {
    private UUID id;
    private UUID vehicleId;
    private LocalDate fillDate;
    private Long odometerReading;
    private BigDecimal fuelVolume;
    private BigDecimal pricePerLiter;
    private BigDecimal totalAmount;
    private BigDecimal mileage;
    private boolean estimated;
    private String notes;
}
