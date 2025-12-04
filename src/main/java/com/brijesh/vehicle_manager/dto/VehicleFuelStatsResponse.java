package com.brijesh.vehicle_manager.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleFuelStatsResponse {
    private BigDecimal totalFuel;      // liters
    private BigDecimal totalCost;      // INR (or chosen currency)
    private Long totalDistance;        // km
    private BigDecimal averageMileage; // km/l
    private BigDecimal costPerKm;      // currency/km
    private Map<String, BigDecimal> extra; // optional detail map
}
