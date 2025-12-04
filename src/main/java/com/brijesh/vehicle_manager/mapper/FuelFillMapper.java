package com.brijesh.vehicle_manager.mapper;

import com.brijesh.vehicle_manager.dto.FuelFillResponse;
import com.brijesh.vehicle_manager.entity.FuelFill;
import org.springframework.stereotype.Component;

/**
 * Single place to map entity -> response. Keeps controllers/services clean.
 */
@Component
public class FuelFillMapper {

    public FuelFillResponse toResponse(FuelFill f) {
        if (f == null) return null;
        return FuelFillResponse.builder()
                .id(f.getId())
                .vehicleId(f.getVehicleId())
                .fillDate(f.getFillDate())
                .odometerReading(f.getOdometerReading())
                .fuelVolume(f.getFuelVolume())
                .pricePerLiter(f.getPricePerLiter())
                .totalAmount(f.getTotalAmount())
                .mileage(f.getMileage())
                .estimated(f.isEstimated())
                .notes(f.getNotes())
                .build();
    }
}

