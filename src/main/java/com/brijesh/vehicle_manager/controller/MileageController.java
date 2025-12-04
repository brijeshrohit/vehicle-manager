package com.brijesh.vehicle_manager.controller;


import com.brijesh.vehicle_manager.dto.MileageMonthlyResponse;
import com.brijesh.vehicle_manager.dto.MileageOverviewResponse;
import com.brijesh.vehicle_manager.dto.MileageTrendResponse;
import com.brijesh.vehicle_manager.service.MileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles/{vehicleId}/mileage")
@RequiredArgsConstructor
public class MileageController {

    private final MileageService mileageService;

    @GetMapping("/overview")
    public MileageOverviewResponse getOverview(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable String vehicleId) {
        return mileageService.getOverview(userId, vehicleId);
    }

    @GetMapping("/trend")
    public MileageTrendResponse getTrend(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable String vehicleId) {
        return mileageService.getTrend(userId, vehicleId);
    }

    @GetMapping("/monthly")
    public MileageMonthlyResponse getMonthly(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable String vehicleId,
            @RequestParam int year) {
        return mileageService.getMonthly(userId, vehicleId, year);
    }
}
