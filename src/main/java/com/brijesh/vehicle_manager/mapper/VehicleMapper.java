package com.brijesh.vehicle_manager.mapper;

import com.brijesh.vehicle_manager.dto.VehicleCreateRequest;
import com.brijesh.vehicle_manager.dto.VehicleResponse;
import com.brijesh.vehicle_manager.dto.VehicleUpdateRequest;
import com.brijesh.vehicle_manager.entity.Vehicle;

import java.time.Instant;
import java.util.UUID;

/**
 * Small mapper helper. We keep it simple (no MapStruct) to avoid extra deps.
 */
public final class VehicleMapper {

    private VehicleMapper() {}

    public static VehicleResponse toResponse(Vehicle v) {
        if (v == null) return null;
        return VehicleResponse.builder()
                .id(v.getId())
                .ownerId(v.getOwnerId())
                .nickname(v.getNickname())
                .registrationNumber(v.getRegistrationNumber())
                .make(v.getMake())
                .model(v.getModel())
                .yearOfManufacture(v.getYearOfManufacture())
                .vehicleType(v.getVehicleType())
                .fuelType(v.getFuelType())
                .purchaseDate(v.getPurchaseDate())
                .initialOdometer(v.getInitialOdometer())
                .currentOdometer(v.getCurrentOdometer())
                .deleted(v.isDeleted())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    public static Vehicle fromCreate(UUID ownerId, VehicleCreateRequest req) {
        Vehicle v = new Vehicle();
        v.setId(UUID.randomUUID());
        v.setOwnerId(ownerId);
        v.setNickname(req.getNickname());
        v.setRegistrationNumber(req.getRegistrationNumber());
        v.setMake(req.getMake());
        v.setModel(req.getModel());
        v.setYearOfManufacture(req.getYearOfManufacture());
        v.setVehicleType(req.getVehicleType());
        v.setFuelType(req.getFuelType());
        v.setPurchaseDate(req.getPurchaseDate());
        v.setInitialOdometer(req.getInitialOdometer());
        v.setCurrentOdometer(req.getCurrentOdometer());
        v.setDeleted(false);
        v.setCreatedAt(Instant.now());
        return v;
    }

    public static void applyUpdate(Vehicle v, VehicleUpdateRequest req) {
        if (req.getNickname() != null) v.setNickname(req.getNickname());
        if (req.getRegistrationNumber() != null) v.setRegistrationNumber(req.getRegistrationNumber());
        if (req.getMake() != null) v.setMake(req.getMake());
        if (req.getModel() != null) v.setModel(req.getModel());
        if (req.getYearOfManufacture() != null) v.setYearOfManufacture(req.getYearOfManufacture());
        if (req.getVehicleType() != null) v.setVehicleType(req.getVehicleType());
        if (req.getFuelType() != null) v.setFuelType(req.getFuelType());
        if (req.getPurchaseDate() != null) v.setPurchaseDate(req.getPurchaseDate());
        if (req.getCurrentOdometer() != null) v.setCurrentOdometer(req.getCurrentOdometer());
        v.setUpdatedAt(Instant.now());
    }
}
