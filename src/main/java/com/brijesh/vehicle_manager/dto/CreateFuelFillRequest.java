package com.brijesh.vehicle_manager.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFuelFillRequest {

    private String vehicleId;
    private LocalDate date;
    private Double liters;
    private Double pricePerLiter;
    private Double odometerReading;
    private Boolean fullTank;
}

