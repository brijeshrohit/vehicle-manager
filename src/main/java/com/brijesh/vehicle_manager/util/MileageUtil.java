package com.brijesh.vehicle_manager.util;

import com.brijesh.vehicle_manager.entity.FuelFill;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Small utility for mileage calculations and rolling averages.
 */
public final class MileageUtil {

    private MileageUtil() {}

    public static BigDecimal computeMileage(long distanceKm, BigDecimal liters) {
        if (liters == null || liters.compareTo(BigDecimal.ZERO) <= 0) return null;
        BigDecimal miles = BigDecimal.valueOf(distanceKm).divide(liters, 6, RoundingMode.HALF_UP);
        return miles.setScale(3, RoundingMode.HALF_UP);
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) return null;
        return a.multiply(b).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Rolling average mileage computed on last N fills with a non-null mileage.
     */
    public static BigDecimal rollingAvgMileage(List<FuelFill> history, int window) {
        if (history == null || history.isEmpty()) return null;
        Optional<BigDecimal> avg = history.stream()
                .filter(f -> f.getMileage() != null && f.getMileage().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(FuelFill::getFillDate).reversed())
                .limit(window)
                .map(FuelFill::getMileage)
                .reduce(BigDecimal::add)
                .map(sum -> sum.divide(BigDecimal.valueOf(Math.min(window,
                        (int) history.stream().filter(f -> f.getMileage() != null).count())), 6, RoundingMode.HALF_UP));

        return avg.orElse(null) == null ? null : avg.get().setScale(3, RoundingMode.HALF_UP);
    }

    /**
     * Rolling average price per liter on last N fills.
     */
    public static BigDecimal rollingAvgPrice(List<FuelFill> history, int window) {
        if (history == null || history.isEmpty()) return null;
        Optional<BigDecimal> avg = history.stream()
                .filter(f -> f.getPricePerLiter() != null && f.getPricePerLiter().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(FuelFill::getFillDate).reversed())
                .limit(window)
                .map(FuelFill::getPricePerLiter)
                .reduce(BigDecimal::add)
                .map(sum -> sum.divide(BigDecimal.valueOf(Math.min(window,
                        (int) history.stream().filter(f -> f.getPricePerLiter() != null).count())), 6, RoundingMode.HALF_UP));
        return avg.orElse(null) == null ? null : avg.get().setScale(2, RoundingMode.HALF_UP);
    }
}
