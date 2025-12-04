package com.brijesh.vehicle_manager.repository;

import com.brijesh.vehicle_manager.entity.FuelFill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FuelFillRepository extends JpaRepository<FuelFill, UUID> {

    /**
     * Single repository method required by your design.
     * Returns chronological history (oldest->newest).
     */
    List<FuelFill> findByVehicleIdOrderByFillDateAsc(UUID vehicleId);
}
