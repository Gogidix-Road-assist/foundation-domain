package com.gogidix.rapidassist.common.domain.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new Vehicle.
 * Contains all fields required to create a vehicle entity with validation.
 */
public class VehicleCreateRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "VIN is required")
    @Size(max = 50, message = "VIN must not exceed 50 characters")
    private String vin;

    @Size(max = 50, message = "Registration number must not exceed 50 characters")
    private String registrationNumber;

    @NotBlank(message = "Make is required")
    @Size(max = 100, message = "Make must not exceed 100 characters")
    private String make;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;

    @Size(max = 50, message = "Year must not exceed 50 characters")
    private String year;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @Pattern(regexp = "SEDAN|SUV|TRUCK|VAN|MOTORCYCLE|BUS|OTHER", message = "Invalid vehicle type")
    private String vehicleType;

    @Size(max = 50, message = "Fuel type must not exceed 50 characters")
    private String fuelType;

    @Size(max = 50, message = "Transmission must not exceed 50 characters")
    private String transmission;

    private Integer mileage;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String tenantId;

    private String organizationId;

    // Default constructor
    public VehicleCreateRequest() {
    }

    // Getters and Setters

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
