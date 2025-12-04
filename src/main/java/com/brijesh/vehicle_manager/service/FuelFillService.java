package com.brijesh.vehicle_manager.service;

import com.brijesh.vehicle_manager.dto.CreateFuelFillRequest;
import com.brijesh.vehicle_manager.dto.FuelFillResponse;

import java.util.List;

public interface FuelFillService {

    FuelFillResponse addFuelFill(CreateFuelFillRequest request);

    List<FuelFillResponse> getFuelHistory(String vehicleId);

    void deleteFuelFill(String fillId);
}
