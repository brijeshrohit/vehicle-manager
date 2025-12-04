package com.brijesh.vehicle_manager.repository;

import com.brijesh.vehicle_manager.entity.FuelFill;
import com.brijesh.vehicle_manager.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuelFillRepository extends JpaRepository<FuelFill, String> {
    List<FuelFill> findByVehicleOrderByDateDesc(Vehicle vehicle);
}
