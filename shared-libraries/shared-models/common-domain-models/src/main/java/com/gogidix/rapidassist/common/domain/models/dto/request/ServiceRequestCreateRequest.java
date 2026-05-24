package com.gogidix.rapidassist.common.domain.models.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.GeoLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a new ServiceRequest.
 * Contains all fields required to create a service request with validation.
 */
public class ServiceRequestCreateRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private String vehicleId;

    @Size(max = 50, message = "Vehicle VIN must not exceed 50 characters")
    private String vehicleVin;

    @Size(max = 50, message = "Vehicle registration must not exceed 50 characters")
    private String vehicleRegistration;

    @NotBlank(message = "Service type is required")
    @Pattern(regexp = "TOWING|JUMP_START|TIRE_CHANGE|FUEL_DELIVERY|LOCKOUT|BATTERY_REPLACEMENT|WINCH_OUT|OTHER", message = "Invalid service type")
    private String serviceType;

    @Pattern(regexp = "LOW|NORMAL|HIGH|EMERGENCY", message = "Priority level must be LOW, NORMAL, HIGH, or EMERGENCY")
    private String priorityLevel = "NORMAL";

    @Pattern(regexp = "NON_URGENT|URGENT|CRITICAL", message = "Urgency level must be NON_URGENT, URGENT, or CRITICAL")
    private String urgencyLevel;

    private GeoLocation location;

    private Address address;

    @Size(max = 500, message = "Location description must not exceed 500 characters")
    private String locationDescription;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedArrival;

    private String tenantId;

    private String organizationId;

    // Default constructor
    public ServiceRequestCreateRequest() {
    }

    // Getters and Setters

    /**
     * Gets the customer ID.
     * @return the customer ID
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID.
     * @param customerId the customer ID to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the vehicle ID.
     * @return the vehicle ID
     */
    public String getVehicleId() {
        return vehicleId;
    }

    /**
     * Sets the vehicle ID.
     * @param vehicleId the vehicle ID to set
     */
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * Gets the vehicle VIN.
     * @return the vehicle VIN
     */
    public String getVehicleVin() {
        return vehicleVin;
    }

    /**
     * Sets the vehicle VIN.
     * @param vehicleVin the vehicle VIN to set
     */
    public void setVehicleVin(String vehicleVin) {
        this.vehicleVin = vehicleVin;
    }

    /**
     * Gets the vehicle registration number.
     * @return the vehicle registration number
     */
    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    /**
     * Sets the vehicle registration number.
     * @param vehicleRegistration the vehicle registration number to set
     */
    public void setVehicleRegistration(String vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }

    /**
     * Gets the service type.
     * @return the service type
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Sets the service type.
     * @param serviceType the service type to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Gets the priority level.
     * @return the priority level
     */
    public String getPriorityLevel() {
        return priorityLevel;
    }

    /**
     * Sets the priority level.
     * @param priorityLevel the priority level to set
     */
    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    /**
     * Gets the urgency level.
     * @return the urgency level
     */
    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    /**
     * Sets the urgency level.
     * @param urgencyLevel the urgency level to set
     */
    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    /**
     * Gets the location.
     * @return the location
     */
    public GeoLocation getLocation() {
        return location;
    }

    /**
     * Sets the location.
     * @param location the location to set
     */
    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    /**
     * Gets the address.
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the address.
     * @param address the address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Gets the location description.
     * @return the location description
     */
    public String getLocationDescription() {
        return locationDescription;
    }

    /**
     * Sets the location description.
     * @param locationDescription the location description to set
     */
    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    /**
     * Gets the phone number.
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the notes.
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes.
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Gets the estimated arrival time.
     * @return the estimated arrival time
     */
    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    /**
     * Sets the estimated arrival time.
     * @param estimatedArrival the estimated arrival time to set
     */
    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    /**
     * Gets the tenant ID.
     * @return the tenant ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets the tenant ID.
     * @param tenantId the tenant ID to set
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets the organization ID.
     * @return the organization ID
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Sets the organization ID.
     * @param organizationId the organization ID to set
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
