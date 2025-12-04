package com.brijesh.vehicle_manager.service.impl;

import com.brijesh.vehicle_manager.dto.FuelFillCreateRequest;
import com.brijesh.vehicle_manager.dto.FuelFillResponse;
import com.brijesh.vehicle_manager.dto.VehicleFuelStatsResponse;
import com.brijesh.vehicle_manager.entity.FuelFill;
import com.brijesh.vehicle_manager.entity.Vehicle;
import com.brijesh.vehicle_manager.exception.ForbiddenException;
import com.brijesh.vehicle_manager.exception.ResourceNotFoundException;
import com.brijesh.vehicle_manager.mapper.FuelFillMapper;
import com.brijesh.vehicle_manager.repository.FuelFillRepository;
import com.brijesh.vehicle_manager.repository.VehicleRepository;
import com.brijesh.vehicle_manager.service.FuelFillService;
import com.brijesh.vehicle_manager.util.MileageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelFillServiceImpl implements FuelFillService {

    private final FuelFillRepository fuelFillRepository;
    private final VehicleRepository vehicleRepository;
    private final FuelFillMapper mapper;

    private static final int ROLLING_WINDOW = 5;

    /**
     * Create and persist a fuel fill. If fuelVolume or pricePerLiter missing, attempt estimation from history.
     * Sets totalAmount and mileage appropriately.
     */
    @Override
    @Transactional
    public FuelFillResponse createFuelFill(UUID userId, FuelFillCreateRequest req) {
        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getOwnerId().equals(userId)) {
            throw new ForbiddenException("You do not own this vehicle");
        }

        // get vehicle history (oldest->newest)
        List<FuelFill> history = fuelFillRepository.findByVehicleIdOrderByFillDateAsc(req.getVehicleId());

        // compute default values / estimations
        BigDecimal fuelVolume = BigDecimal.valueOf(req.getLiters());
        BigDecimal pricePerLiter = BigDecimal.valueOf(req.getPricePerLiter());
        boolean estimated = false;

        // estimate fuelVolume if null (distance since last known fill / rolling avg mileage)
        if (fuelVolume == null) {
            // find last fill (latest before this date)
            var prevOpt = history.stream()
                    .filter(f -> !f.getFillDate().isAfter(req.getFillDate()))
                    .max(Comparator.comparing(FuelFill::getFillDate));

            if (prevOpt.isPresent()) {
                FuelFill last = prevOpt.get();
                if (last.getOdometerReading() != null) {
                    long distance = req.getOdometer() - last.getOdometerReading();
                    BigDecimal rollingMileage = MileageUtil.rollingAvgMileage(history, ROLLING_WINDOW);

                    if (rollingMileage != null && rollingMileage.compareTo(BigDecimal.ZERO) > 0) {
                        fuelVolume = BigDecimal.valueOf(distance).divide(rollingMileage, 6, BigDecimal.ROUND_HALF_UP)
                                .setScale(3, BigDecimal.ROUND_HALF_UP);
                        estimated = true;
                    }
                }
            }
        }

        // estimate pricePerLiter if null (rolling average)
        if (pricePerLiter == null) {
            BigDecimal rollingPrice = MileageUtil.rollingAvgPrice(history, ROLLING_WINDOW);
            if (rollingPrice != null) {
                pricePerLiter = rollingPrice;
                estimated = true;
            }
        }

        // If required values are still missing (no history), require filling by user
        if (fuelVolume == null || pricePerLiter == null) {
            throw new IllegalArgumentException("Insufficient data to estimate fields. Provide fuelVolume and pricePerLiter for the first fill.");
        }

        BigDecimal totalAmount = MileageUtil.multiply(fuelVolume, pricePerLiter);

        // compute mileage using previous odometer reading if possible
        BigDecimal mileage = null;
        var prevOpt = history.stream()
                .filter(f -> !f.getFillDate().isAfter(req.getFillDate()))
                .max(Comparator.comparing(FuelFill::getFillDate));
        if (prevOpt.isPresent()) {
            FuelFill last = prevOpt.get();
            if (last.getOdometerReading() != null && fuelVolume.compareTo(BigDecimal.ZERO) > 0) {
                long distance = req.getOdometer() - last.getOdometerReading();
                if (distance > 0) {
                    mileage = MileageUtil.computeMileage(distance, fuelVolume);
                }
            }
        }

        // --- Update Vehicle current odometer ---
        if (vehicle.getCurrentOdometer() == null ||
                req.getOdometer() > vehicle.getCurrentOdometer()) {

            vehicle.setCurrentOdometer(req.getOdometer());
            vehicle.setUpdatedAt(Instant.now());
            vehicleRepository.save(vehicle);
        }


        FuelFill f = FuelFill.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .vehicleId(req.getVehicleId())
                .fillDate(req.getFillDate())
                .odometerReading(req.getOdometer())
                .fuelVolume(fuelVolume)
                .pricePerLiter(pricePerLiter)
                .totalAmount(totalAmount)
                .mileage(mileage)
                .estimated(estimated)
                .createdAt(Instant.now())
                .build();

        fuelFillRepository.save(f);
        return mapper.toResponse(f);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuelFillResponse> getFillsForVehicle(UUID userId, UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        if (!vehicle.getOwnerId().equals(userId)) throw new ForbiddenException("You do not own this vehicle");

        return fuelFillRepository.findByVehicleIdOrderByFillDateAsc(vehicleId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleFuelStatsResponse computeVehicleStats(UUID userId, UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        if (!vehicle.getOwnerId().equals(userId)) throw new ForbiddenException("You do not own this vehicle");

        List<FuelFill> history = fuelFillRepository.findByVehicleIdOrderByFillDateAsc(vehicleId);

        if (history.isEmpty()) {
            return VehicleFuelStatsResponse.builder()
                    .totalFuel(BigDecimal.ZERO)
                    .totalCost(BigDecimal.ZERO)
                    .totalDistance(0L)
                    .averageMileage(null)
                    .costPerKm(null)
                    .extra(null)
                    .build();
        }

        BigDecimal totalFuel = history.stream()
                .map(f -> f.getFuelVolume() == null ? BigDecimal.ZERO : f.getFuelVolume())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = history.stream()
                .map(f -> f.getTotalAmount() == null ? BigDecimal.ZERO : f.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalDistance = 0L;
        if (history.size() >= 2) {
            long first = history.get(0).getOdometerReading();
            long last = history.get(history.size() - 1).getOdometerReading();
            totalDistance = last - first;
        }

        BigDecimal averageMileage = null;
        if (totalFuel.compareTo(BigDecimal.ZERO) > 0) {
            averageMileage = BigDecimal.valueOf(totalDistance).divide(totalFuel, 6, BigDecimal.ROUND_HALF_UP).setScale(3, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal costPerKm = null;
        if (totalDistance > 0) {
            costPerKm = totalCost.divide(BigDecimal.valueOf(totalDistance), 6, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        return VehicleFuelStatsResponse.builder()
                .totalFuel(totalFuel.setScale(3, BigDecimal.ROUND_HALF_UP))
                .totalCost(totalCost.setScale(2, BigDecimal.ROUND_HALF_UP))
                .totalDistance(totalDistance)
                .averageMileage(averageMileage)
                .costPerKm(costPerKm)
                .extra(null)
                .build();
    }
}
