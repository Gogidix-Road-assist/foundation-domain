package com.gogidix.rapidassist.common.domain.models.dto.request;

import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.GeoLocation;
import com.gogidix.rapidassist.common.domain.models.common.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new Provider.
 * Contains all fields required to create a service provider entity with validation.
 */
public class ProviderCreateRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @Size(max = 100, message = "Provider number must not exceed 100 characters")
    private String providerNumber;

    @NotBlank(message = "Business name is required")
    @Size(max = 200, message = "Business name must not exceed 200 characters")
    private String businessName;

    @Size(max = 200, message = "Legal name must not exceed 200 characters")
    private String legalName;

    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;

    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    private PhoneNumber primaryPhone;

    private PhoneNumber secondaryPhone;

    private Address address;

    private GeoLocation headquartersLocation;

    @Size(max = 20, message = "Tax ID must not exceed 20 characters")
    private String taxId;

    @Size(max = 100, message = "License number must not exceed 100 characters")
    private String licenseNumber;

    @Pattern(regexp = "INDIVIDUAL|PARTNERSHIP|CORPORATION|LLC", message = "Invalid business type")
    private String businessType;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String tenantId;

    private String organizationId;

    // Default constructor
    public ProviderCreateRequest() {
    }

    // Getters and Setters

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
