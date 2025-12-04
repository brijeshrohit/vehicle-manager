package com.brijesh.vehicle_manager.service;

import com.brijesh.vehicle_manager.dto.FuelFillCreateRequest;
import com.brijesh.vehicle_manager.dto.FuelFillResponse;
import com.brijesh.vehicle_manager.dto.VehicleFuelStatsResponse;

import java.util.List;
import java.util.UUID;

public interface FuelFillService {

    FuelFillResponse createFuelFill(UUID userId, FuelFillCreateRequest req);

    List<FuelFillResponse> getFillsForVehicle(UUID userId, UUID vehicleId);

    VehicleFuelStatsResponse computeVehicleStats(UUID userId, UUID vehicleId);
}
