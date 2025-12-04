package com.brijesh.vehicle_manager.service.impl;

import com.brijesh.vehicle_manager.dto.CreateFuelFillRequest;
import com.brijesh.vehicle_manager.dto.FuelFillResponse;
import com.brijesh.vehicle_manager.entity.FuelFill;
import com.brijesh.vehicle_manager.entity.Vehicle;
import com.brijesh.vehicle_manager.repository.FuelFillRepository;
import com.brijesh.vehicle_manager.repository.VehicleRepository;
import com.brijesh.vehicle_manager.service.FuelFillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FuelFillServiceImpl implements FuelFillService {

    private final VehicleRepository vehicleRepository;
    private final FuelFillRepository fuelFillRepository;

    @Override
    public FuelFillResponse addFuelFill(CreateFuelFillRequest request) {

        Vehicle vehicle = vehicleRepository.findById(UUID.fromString(request.getVehicleId()))
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        double totalCost = request.getLiters() * request.getPricePerLiter();

        FuelFill fill = FuelFill.builder()
                .vehicle(vehicle)
                .date(request.getDate())
                .liters(request.getLiters())
                .pricePerLiter(request.getPricePerLiter())
                .totalCost(totalCost)
                .odometerReading(request.getOdometerReading())
                .fullTank(request.getFullTank())
                .build();

        fill = fuelFillRepository.save(fill);

        return toResponse(fill);
    }

    @Override
    public List<FuelFillResponse> getFuelHistory(String vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(UUID.fromString(vehicleId))
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        return fuelFillRepository.findByVehicleOrderByDateDesc(vehicle)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteFuelFill(String fillId) {
        fuelFillRepository.deleteById(fillId);
    }

    private FuelFillResponse toResponse(FuelFill f) {
        return FuelFillResponse.builder()
                .id(f.getId())
                .vehicleId(String.valueOf(f.getVehicle().getId()))
                .date(f.getDate())
                .liters(f.getLiters())
                .pricePerLiter(f.getPricePerLiter())
                .totalCost(f.getTotalCost())
                .odometerReading(f.getOdometerReading())
                .fullTank(f.getFullTank())
                .build();
    }
}
