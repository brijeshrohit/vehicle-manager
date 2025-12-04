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
public class MileageOverviewResponse {
    private BigDecimal lastFillMileage;
    private BigDecimal averageMileage;
    private BigDecimal bestMileage;
    private BigDecimal worstMileage;
    private BigDecimal totalDistance;
    private int totalFills;
}