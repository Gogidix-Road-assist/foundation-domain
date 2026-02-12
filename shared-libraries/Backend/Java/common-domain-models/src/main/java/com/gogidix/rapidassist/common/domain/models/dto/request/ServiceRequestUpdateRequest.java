package com.gogidix.rapidassist.common.domain.models.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.GeoLocation;
import com.gogidix.rapidassist.common.domain.models.common.Money;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for updating an existing ServiceRequest.
 * All fields are optional to support partial updates.
 */
public class ServiceRequestUpdateRequest {

    @Pattern(regexp = "PENDING|ASSIGNED|IN_PROGRESS|COMPLETED|CANCELLED", message = "Invalid status")
    private String status;

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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualArrival;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cancelledAt;

    @Size(max = 50, message = "Cancellation reason must not exceed 50 characters")
    private String cancellationReason;

    private String assignedProviderId;

    private String assignedDriverId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime assignedAt;

    private Money estimatedCost;

    private Money actualCost;

    private Money quotedPrice;

    @Pattern(regexp = "PENDING|PAID|FAILED|REFUNDED", message = "Invalid payment status")
    private String paymentStatus;

    @Size(max = 100, message = "Payment reference must not exceed 100 characters")
    private String paymentReference;

    private Integer rating;

    @Size(max = 1000, message = "Feedback must not exceed 1000 characters")
    private String feedback;

    // Default constructor
    public ServiceRequestUpdateRequest() {
    }

    // Getters and Setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public LocalDateTime getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(LocalDateTime actualArrival) {
        this.actualArrival = actualArrival;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getAssignedProviderId() {
        return assignedProviderId;
    }

    public void setAssignedProviderId(String assignedProviderId) {
        this.assignedProviderId = assignedProviderId;
    }

    public String getAssignedDriverId() {
        return assignedDriverId;
    }

    public void setAssignedDriverId(String assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Money getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Money estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Money getActualCost() {
        return actualCost;
    }

    public void setActualCost(Money actualCost) {
        this.actualCost = actualCost;
    }

    public Money getQuotedPrice() {
        return quotedPrice;
    }

    public void setQuotedPrice(Money quotedPrice) {
        this.quotedPrice = quotedPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
