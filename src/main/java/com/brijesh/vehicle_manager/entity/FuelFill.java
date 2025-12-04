package com.brijesh.vehicle_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "fuel_fills")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelFill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double liters;

    @Column(nullable = false)
    private Double pricePerLiter;

    @Column(nullable = false)
    private Double totalCost;

    @Column(nullable = false)
    private Double odometerReading;

    @Column
    private Boolean fullTank; // TRUE = full tank or FALSE = partial fill
}
