package com.brijesh.vehicle_manager.repository;

import com.brijesh.vehicle_manager.entity.FuelFill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FuelFillRepository extends JpaRepository<FuelFill, UUID> {

    /**
     * Single repository method required by your design.
     * Returns chronological history (oldest->newest).
     */
    List<FuelFill> findByVehicleIdOrderByFillDateAsc(UUID vehicleId);

    @Query("SELECT f FROM FuelFill f WHERE f.vehicle.id = :vehicleId AND YEAR(f.fillDate) = :year ORDER BY f.fillDate ASC")
    List<FuelFill> findByVehicleIdAndYear(@Param("vehicleId") String vehicleId, @Param("year") int year);

}
