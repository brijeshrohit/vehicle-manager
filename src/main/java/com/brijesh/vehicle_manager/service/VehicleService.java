package com.brijesh.vehicle_manager.service;

import com.brijesh.vehicle_manager.dto.VehicleCreateRequest;
import com.brijesh.vehicle_manager.dto.VehicleResponse;
import com.brijesh.vehicle_manager.dto.VehicleUpdateRequest;
import com.brijesh.vehicle_manager.entity.Vehicle;
import com.brijesh.vehicle_manager.exception.VehicleNotFoundException;
import com.brijesh.vehicle_manager.mapper.VehicleMapper;
import com.brijesh.vehicle_manager.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Vehicle business logic and ownership checks.
 * The service accepts ownerId for each operation (parsed from JWT).
 */
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    /**
     * Create a vehicle for owner.
     */
    @Transactional
    public VehicleResponse createVehicle(UUID ownerId, VehicleCreateRequest req) {
        Vehicle v = VehicleMapper.fromCreate(ownerId, req);
        Vehicle saved = vehicleRepository.save(v);
        return VehicleMapper.toResponse(saved);
    }

    /**
     * List vehicles for owner.
     */
    @Transactional(readOnly = true)
    public List<VehicleResponse> listVehicles(UUID ownerId) {
        return vehicleRepository.findAllByOwnerIdAndDeletedFalse(ownerId)
                .stream().map(VehicleMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Get vehicle by id verifying ownership.
     */
    @Transactional(readOnly = true)
    public VehicleResponse getVehicle(UUID ownerId, UUID vehicleId) {
        Vehicle v = vehicleRepository.findByIdAndOwnerIdAndDeletedFalse(vehicleId, ownerId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        return VehicleMapper.toResponse(v);
    }

    /**
     * Update vehicle (only owner).
     */
    @Transactional
    public VehicleResponse updateVehicle(UUID ownerId, UUID vehicleId, VehicleUpdateRequest req) {
        Vehicle v = vehicleRepository.findByIdAndOwnerIdAndDeletedFalse(vehicleId, ownerId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found or access denied"));
        VehicleMapper.applyUpdate(v, req);
        Vehicle saved = vehicleRepository.save(v);
        return VehicleMapper.toResponse(saved);
    }

    /**
     * Soft-delete vehicle.
     */
    @Transactional
    public void deleteVehicle(UUID ownerId, UUID vehicleId) {
        Vehicle v = vehicleRepository.findByIdAndOwnerIdAndDeletedFalse(vehicleId, ownerId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found or access denied"));
        v.setDeleted(true);
        vehicleRepository.save(v);
    }
}
