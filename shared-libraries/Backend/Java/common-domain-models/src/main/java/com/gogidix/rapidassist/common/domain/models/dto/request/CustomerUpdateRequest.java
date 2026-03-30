package com.gogidix.rapidassist.common.domain.models.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for updating an existing Customer.
 * All fields are optional to support partial updates.
 */
public class CustomerUpdateRequest {

    @Size(max = 100, message = "Customer number must not exceed 100 characters")
    private String customerNumber;

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    private PhoneNumber primaryPhone;

    private PhoneNumber secondaryPhone;

    private Address address;

    @Pattern(regexp = "INDIVIDUAL|CORPORATE|GOVERNMENT", message = "Customer type must be INDIVIDUAL, CORPORATE, or GOVERNMENT")
    private String customerType;

    @Pattern(regexp = "STANDARD|SILVER|GOLD|PLATINUM", message = "Membership level must be STANDARD, SILVER, GOLD, or PLATINUM")
    private String membershipLevel;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime membershipExpiry;

    private Integer creditLimit;

    private Integer currentBalance;

    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED|BLOCKED", message = "Status must be ACTIVE, INACTIVE, SUSPENDED, or BLOCKED")
    private String status;

    private String organizationId;

    private String corporateAccountId;

    // Default constructor
    public CustomerUpdateRequest() {
    }

    // Getters and Setters

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public LocalDateTime getMembershipExpiry() {
        return membershipExpiry;
    }

    public void setMembershipExpiry(LocalDateTime membershipExpiry) {
        this.membershipExpiry = membershipExpiry;
    }

    public Integer getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Integer creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getCorporateAccountId() {
        return corporateAccountId;
    }

    public void setCorporateAccountId(String corporateAccountId) {
        this.corporateAccountId = corporateAccountId;
    }
}
