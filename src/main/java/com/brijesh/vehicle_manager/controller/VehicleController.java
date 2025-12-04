package com.brijesh.vehicle_manager.controller;

import com.brijesh.vehicle_manager.dto.VehicleCreateRequest;
import com.brijesh.vehicle_manager.dto.VehicleResponse;
import com.brijesh.vehicle_manager.dto.VehicleUpdateRequest;
import com.brijesh.vehicle_manager.service.impl.VehicleServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for vehicles.
 * Authentication: we set the Authentication principal as the String userId in SecurityConfig filter.
 * So we parse principal.getName() -> UUID.
 */
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleServiceImpl vehicleService;

    private UUID getCurrentUserId(Authentication auth) {
        if (auth == null || auth.getName() == null) throw new RuntimeException("Unauthenticated");
        return UUID.fromString(auth.getName());
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(Authentication auth, @Valid @RequestBody VehicleCreateRequest req) {
        UUID userId = getCurrentUserId(auth);
        VehicleResponse resp = vehicleService.createVehicle(userId, req);
        return ResponseEntity.created(URI.create("/api/vehicles/" + resp.getId())).body(resp);
    }

    @GetMapping("/my")
    public ResponseEntity<List<VehicleResponse>> listMyVehicles(Authentication auth) {
        UUID userId = getCurrentUserId(auth);
        return ResponseEntity.ok(vehicleService.listVehicles(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicle(Authentication auth, @PathVariable("id") UUID id) {
        UUID userId = getCurrentUserId(auth);
        return ResponseEntity.ok(vehicleService.getVehicle(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(Authentication auth, @PathVariable("id") UUID id,
                                                         @Valid @RequestBody VehicleUpdateRequest req) {
        UUID userId = getCurrentUserId(auth);
        return ResponseEntity.ok(vehicleService.updateVehicle(userId, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(Authentication auth, @PathVariable("id") UUID id) {
        UUID userId = getCurrentUserId(auth);
        vehicleService.deleteVehicle(userId, id);
        return ResponseEntity.noContent().build();
    }
}
