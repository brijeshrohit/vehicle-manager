package com.brijesh.vehicle_manager.service;

import com.brijesh.vehicle_manager.dto.MileageMonthlyResponse;
import com.brijesh.vehicle_manager.dto.MileageOverviewResponse;
import com.brijesh.vehicle_manager.dto.MileageTrendResponse;
import com.brijesh.vehicle_manager.entity.FuelFill;
import com.brijesh.vehicle_manager.entity.Vehicle;

import java.math.BigDecimal;
import java.util.List;

public interface MileageService {
    // ------------  VALIDATION  ----------
    Vehicle validateVehicleAccess(String userId, String vehicleId);

    // ------------  UTILITY  ----------
    List<BigDecimal> computeMileage(List<FuelFill> fills);

    // ------------  OVERVIEW  ----------
    MileageOverviewResponse getOverview(String userId, String vehicleId);

    // ------------  TREND  ----------
    MileageTrendResponse getTrend(String userId, String vehicleId);

    // ------------  MONTHLY  ----------
    MileageMonthlyResponse getMonthly(String userId, String vehicleId, int year);
}
