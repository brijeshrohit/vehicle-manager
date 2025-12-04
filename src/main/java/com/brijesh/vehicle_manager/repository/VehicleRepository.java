package com.brijesh.vehicle_manager.repository;

import com.brijesh.vehicle_manager.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    // List vehicles for a user (non-deleted)
    List<Vehicle> findAllByOwnerIdAndDeletedFalse(UUID ownerId);

    Optional<Vehicle> findByIdAndOwnerIdAndDeletedFalse(UUID id, UUID ownerId);
}
