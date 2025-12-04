package com.brijesh.vehicle_manager.controller;

import com.brijesh.vehicle_manager.dto.FuelFillCreateRequest;
import com.brijesh.vehicle_manager.dto.FuelFillResponse;
import com.brijesh.vehicle_manager.dto.VehicleFuelStatsResponse;
import com.brijesh.vehicle_manager.service.FuelFillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fuel")
@RequiredArgsConstructor
public class FuelFillController {

    private final FuelFillService fuelFillService;

    // Create fill (auth required)
    @PostMapping("/add")
    public ResponseEntity<FuelFillResponse> addFill(Authentication auth,
                                                    @Valid @RequestBody FuelFillCreateRequest req) {
        UUID userId = UUID.fromString(auth.getName());
        FuelFillResponse resp = fuelFillService.createFuelFill(userId, req);
        return ResponseEntity.ok(resp);
    }

    // List fills for vehicle (auth required)
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<FuelFillResponse>> getFills(Authentication auth,
                                                           @PathVariable UUID vehicleId) {
        UUID userId = UUID.fromString(auth.getName());
        List<FuelFillResponse> resp = fuelFillService.getFillsForVehicle(userId, vehicleId);
        return ResponseEntity.ok(resp);
    }

    // Stats for vehicle
    @GetMapping("/vehicle/{vehicleId}/stats")
    public ResponseEntity<VehicleFuelStatsResponse> getStats(Authentication auth,
                                                             @PathVariable UUID vehicleId) {
        UUID userId = UUID.fromString(auth.getName());
        VehicleFuelStatsResponse stats = fuelFillService.computeVehicleStats(userId, vehicleId);
        return ResponseEntity.ok(stats);
    }
}

