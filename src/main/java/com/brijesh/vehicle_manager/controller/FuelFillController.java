package com.brijesh.vehicle_manager.controller;

import com.brijesh.vehicle_manager.dto.CreateFuelFillRequest;
import com.brijesh.vehicle_manager.dto.FuelFillResponse;
import com.brijesh.vehicle_manager.service.FuelFillService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fuel")
@RequiredArgsConstructor
public class FuelFillController {

    private final FuelFillService fuelFillService;

    @PostMapping("/add")
    public ResponseEntity<FuelFillResponse> addFuelFill(@RequestBody CreateFuelFillRequest request) {
        return ResponseEntity.ok(fuelFillService.addFuelFill(request));
    }

    @GetMapping("/history/{vehicleId}")
    public ResponseEntity<List<FuelFillResponse>> getFuelHistory(@PathVariable String vehicleId) {
        return ResponseEntity.ok(fuelFillService.getFuelHistory(vehicleId));
    }

    @DeleteMapping("/{fillId}")
    public ResponseEntity<String> deleteFuelFill(@PathVariable String fillId) {
        fuelFillService.deleteFuelFill(fillId);
        return ResponseEntity.ok("FUEL_FILL_DELETED");
    }
}
