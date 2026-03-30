package com.gogidix.rapidassist.common.domain.models.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a new Customer.
 * Contains all fields required to create a customer entity with validation.
 */
public class CustomerCreateRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @Size(max = 100, message = "Customer number must not exceed 100 characters")
    private String customerNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    private PhoneNumber primaryPhone;

    private PhoneNumber secondaryPhone;

    private Address address;

    @Pattern(regexp = "INDIVIDUAL|CORPORATE|GOVERNMENT", message = "Customer type must be INDIVIDUAL, CORPORATE, or GOVERNMENT")
    private String customerType = "INDIVIDUAL";

    @Pattern(regexp = "STANDARD|SILVER|GOLD|PLATINUM", message = "Membership level must be STANDARD, SILVER, GOLD, or PLATINUM")
    private String membershipLevel;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime membershipExpiry;

    private Integer creditLimit;

    private String tenantId;

    private String organizationId;

    private String corporateAccountId;

    // Default constructor
    public CustomerCreateRequest() {
    }

    // Getters and Setters

    /**
     * Gets the user ID associated with this customer.
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with this customer.
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the customer number.
     * @return the customer number
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customer number.
     * @param customerNumber the customer number to set
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the first name.
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name.
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email address.
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the primary phone number.
     * @return the primary phone number
     */
    public PhoneNumber getPrimaryPhone() {
        return primaryPhone;
    }

    /**
     * Sets the primary phone number.
     * @param primaryPhone the primary phone number to set
     */
    public void setPrimaryPhone(PhoneNumber primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    /**
     * Gets the secondary phone number.
     * @return the secondary phone number
     */
    public PhoneNumber getSecondaryPhone() {
        return secondaryPhone;
    }

    /**
     * Sets the secondary phone number.
     * @param secondaryPhone the secondary phone number to set
     */
    public void setSecondaryPhone(PhoneNumber secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
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
     * Gets the customer type (INDIVIDUAL, CORPORATE, GOVERNMENT).
     * @return the customer type
     */
    public String getCustomerType() {
        return customerType;
    }

    /**
     * Sets the customer type.
     * @param customerType the customer type to set
     */
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    /**
     * Gets the membership level.
     * @return the membership level
     */
    public String getMembershipLevel() {
        return membershipLevel;
    }

    /**
     * Sets the membership level.
     * @param membershipLevel the membership level to set
     */
    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    /**
     * Gets the membership expiry date.
     * @return the membership expiry date
     */
    public LocalDateTime getMembershipExpiry() {
        return membershipExpiry;
    }

    /**
     * Sets the membership expiry date.
     * @param membershipExpiry the membership expiry date to set
     */
    public void setMembershipExpiry(LocalDateTime membershipExpiry) {
        this.membershipExpiry = membershipExpiry;
    }

    /**
     * Gets the credit limit in cents/base currency.
     * @return the credit limit
     */
    public Integer getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the credit limit.
     * @param creditLimit the credit limit to set
     */
    public void setCreditLimit(Integer creditLimit) {
        this.creditLimit = creditLimit;
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

    /**
     * Gets the corporate account ID.
     * @return the corporate account ID
     */
    public String getCorporateAccountId() {
        return corporateAccountId;
    }

    /**
     * Sets the corporate account ID.
     * @param corporateAccountId the corporate account ID to set
     */
    public void setCorporateAccountId(String corporateAccountId) {
        this.corporateAccountId = corporateAccountId;
    }
}
