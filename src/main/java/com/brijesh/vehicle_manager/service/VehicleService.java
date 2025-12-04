package com.brijesh.vehicle_manager.service;

import com.brijesh.vehicle_manager.dto.VehicleCreateRequest;
import com.brijesh.vehicle_manager.dto.VehicleResponse;
import com.brijesh.vehicle_manager.dto.VehicleUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface VehicleService {
    @Transactional
    VehicleResponse createVehicle(UUID ownerId, VehicleCreateRequest req);

    @Transactional(readOnly = true)
    List<VehicleResponse> listVehicles(UUID ownerId);

    @Transactional(readOnly = true)
    VehicleResponse getVehicle(UUID ownerId, UUID vehicleId);

    @Transactional
    VehicleResponse updateVehicle(UUID ownerId, UUID vehicleId, VehicleUpdateRequest req);

    @Transactional
    void deleteVehicle(UUID ownerId, UUID vehicleId);
}
