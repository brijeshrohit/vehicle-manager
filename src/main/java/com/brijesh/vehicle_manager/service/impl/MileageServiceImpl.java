package com.brijesh.vehicle_manager.service.impl;

import com.brijesh.vehicle_manager.dto.*;
import com.brijesh.vehicle_manager.entity.FuelFill;
import com.brijesh.vehicle_manager.entity.Vehicle;
import com.brijesh.vehicle_manager.exception.ResourceNotFoundException;
import com.brijesh.vehicle_manager.repository.FuelFillRepository;
import com.brijesh.vehicle_manager.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MileageServiceImpl implements com.brijesh.vehicle_manager.service.MileageService {

    private final FuelFillRepository fuelFillRepo;
    private final VehicleRepository vehicleRepo;

    // ------------  VALIDATION  ----------
    @Override
    public Vehicle validateVehicleAccess(String userId, String vehicleId) {
        return vehicleRepo.findById(UUID.fromString(vehicleId))
                .filter(v -> v.getOwnerId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found or not accessible"));
    }

    // ------------  UTILITY  ----------
    @Override
    public List<BigDecimal> computeMileage(List<FuelFill> fills) {

        List<BigDecimal> mileages = new ArrayList<>();

        for (int i = 1; i < fills.size(); i++) {
            FuelFill prev = fills.get(i - 1);
            FuelFill curr = fills.get(i);

            BigDecimal distance = BigDecimal.valueOf( curr.getOdometerReading() - (prev.getOdometerReading()));
            BigDecimal mileage = distance.divide(curr.getFuelVolume(), 2, RoundingMode.HALF_UP);

            mileages.add(mileage);
        }

        return mileages;
    }

    // ------------  OVERVIEW  ----------
    @Override
    public MileageOverviewResponse getOverview(String userId, String vehicleId) {

        validateVehicleAccess(userId, vehicleId);

        List<FuelFill> fills = fuelFillRepo.findByVehicleIdOrderByFillDateAsc(UUID.fromString(vehicleId));

        if (fills.size() < 2) {
            return MileageOverviewResponse.builder()
                    .lastFillMileage(BigDecimal.ZERO)
                    .averageMileage(BigDecimal.ZERO)
                    .bestMileage(BigDecimal.ZERO)
                    .worstMileage(BigDecimal.ZERO)
                    .totalDistance(BigDecimal.ZERO)
                    .totalFills(fills.size())
                    .build();
        }

        List<BigDecimal> mileages = computeMileage(fills);

        BigDecimal last = mileages.get(mileages.size() - 1);
        BigDecimal avg = mileages.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(mileages.size()), 2, RoundingMode.HALF_UP);

        BigDecimal best = mileages.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        BigDecimal worst = mileages.stream().min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);

        BigDecimal totalDistance = BigDecimal.valueOf(fills.get(fills.size() - 1).getOdometerReading() -
                fills.get(0).getOdometerReading());

        return MileageOverviewResponse.builder()
                .lastFillMileage(last)
                .averageMileage(avg)
                .bestMileage(best)
                .worstMileage(worst)
                .totalDistance(totalDistance)
                .totalFills(fills.size())
                .build();
    }

    // ------------  TREND  ----------
    @Override
    public MileageTrendResponse getTrend(String userId, String vehicleId) {

        validateVehicleAccess(userId, vehicleId);

        List<FuelFill> fills = fuelFillRepo.findByVehicleIdOrderByFillDateAsc(UUID.fromString(vehicleId));

        List<MileagePoint> points = new ArrayList<>();

        for (int i = 1; i < fills.size(); i++) {
            FuelFill prev = fills.get(i - 1);
            FuelFill curr = fills.get(i);

            BigDecimal distance = BigDecimal.valueOf(curr.getOdometerReading() - prev.getOdometerReading());
            BigDecimal mileage = distance.divide(curr.getFuelVolume(), 2, RoundingMode.HALF_UP);

            points.add(MileagePoint.builder()
                    .fillDate(curr.getFillDate())
                    .mileage(mileage)
                    .build());
        }

        return MileageTrendResponse.builder().trend(points).build();
    }

    // ------------  MONTHLY  ----------
    @Override
    public MileageMonthlyResponse getMonthly(String userId, String vehicleId, int year) {

        validateVehicleAccess(userId, vehicleId);

        List<FuelFill> fills = fuelFillRepo.findByVehicleIdAndYear(vehicleId, year);

        Map<String, MonthlyMileageData> map = new LinkedHashMap<>();

        // initialize map for all 12 months
        for (Month m : Month.values()) {
            map.put(m.name(), new MonthlyMileageData(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            ));
        }

        // compute month-wise
        for (int i = 1; i < fills.size(); i++) {

            FuelFill prev = fills.get(i - 1);
            FuelFill curr = fills.get(i);

            String month = curr.getFillDate().getMonth().name();

            BigDecimal distance = BigDecimal.valueOf(curr.getOdometerReading() - prev.getOdometerReading());
            BigDecimal fuel = curr.getFuelVolume();
            BigDecimal mileage = distance.divide(fuel, 2, RoundingMode.HALF_UP);

            MonthlyMileageData old = map.get(month);

            map.put(month,
                    new MonthlyMileageData(
                            // average mileage update
                            old.getAvgMileage().add(mileage),
                            old.getDistanceTravelled().add(distance),
                            old.getFuelConsumed().add(fuel)
                    ));
        }

        // finalize average (divide total mileage by number of entries per month)
        for (String month : map.keySet()) {

            MonthlyMileageData data = map.get(month);

            if (data.getDistanceTravelled().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal avgMileage =
                        data.getDistanceTravelled().divide(
                                data.getFuelConsumed(), 2, RoundingMode.HALF_UP);

                data.setAvgMileage(avgMileage);
            }
        }

        return MileageMonthlyResponse.builder().data(map).build();
    }
}

