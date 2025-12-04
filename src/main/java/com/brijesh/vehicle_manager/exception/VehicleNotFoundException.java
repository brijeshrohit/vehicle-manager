package com.brijesh.vehicle_manager.exception;

/** Thrown when vehicle not found or access denied. */
public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String message) { super(message); }
}
