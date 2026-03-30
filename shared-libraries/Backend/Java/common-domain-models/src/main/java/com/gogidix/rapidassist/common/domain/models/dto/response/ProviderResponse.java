package com.gogidix.rapidassist.common.domain.models.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.GeoLocation;
import com.gogidix.rapidassist.common.domain.models.common.PhoneNumber;

import java.time.LocalDateTime;

/**
 * Response DTO for Provider entity.
 * Contains provider information suitable for API responses.
 */
public class ProviderResponse {

    private String id;
    private String userId;
    private String providerNumber;
    private String businessName;
    private String legalName;
    private String contactPerson;
    private String email;
    private PhoneNumber primaryPhone;
    private PhoneNumber secondaryPhone;
    private Address address;
    private GeoLocation headquartersLocation;
    private String taxId;
    private String licenseNumber;
    private String businessType;
    private String description;
    private Double averageRating;
    private Integer totalServicesCompleted;
    private String status;
    private String tenantId;
    private String organizationId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private String createdBy;

    // Default constructor
    public ProviderResponse() {
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProviderNumber() {
        return providerNumber;
    }

    public void setProviderNumber(String providerNumber) {
        this.providerNumber = providerNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PhoneNumber getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(PhoneNumber primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public PhoneNumber getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(PhoneNumber secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public GeoLocation getHeadquartersLocation() {
        return headquartersLocation;
    }

    public void setHeadquartersLocation(GeoLocation headquartersLocation) {
        this.headquartersLocation = headquartersLocation;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalServicesCompleted() {
        return totalServicesCompleted;
    }

    public void setTotalServicesCompleted(Integer totalServicesCompleted) {
        this.totalServicesCompleted = totalServicesCompleted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
