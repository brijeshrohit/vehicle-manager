package com.brijesh.vehicle_manager.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelFillResponse {

    private String id;
    private String vehicleId;
    private LocalDate date;
    private Double liters;
    private Double pricePerLiter;
    private Double totalCost;
    private Double odometerReading;
    private Boolean fullTank;
}
