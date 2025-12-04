package com.brijesh.vehicle_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyMileageData {
    private BigDecimal avgMileage;
    private BigDecimal distanceTravelled;
    private BigDecimal fuelConsumed;
}
