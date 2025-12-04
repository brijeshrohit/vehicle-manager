package com.brijesh.vehicle_manager.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String vehicleNotFound) {
        super(vehicleNotFound);
    }
}
