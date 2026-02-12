package com.gogidix.rapidassist.common.domain.models.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.PhoneNumber;

import java.time.LocalDateTime;

/**
 * Response DTO for Customer entity.
 * Contains customer information suitable for API responses.
 */
public class CustomerResponse {

    private String id;
    private String userId;
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String email;
    private PhoneNumber primaryPhone;
    private PhoneNumber secondaryPhone;
    private Address address;
    private String customerType;
    private String membershipLevel;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime membershipExpiry;
    private Integer creditLimit;
    private Integer currentBalance;
    private String status;
    private String tenantId;
    private String organizationId;
    private String corporateAccountId;
    private Integer totalRequests;
    private Integer completedRequests;
    private Integer cancelledRequests;
    private Double averageRating;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastServiceDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private String createdBy;

    // Default constructor
    public CustomerResponse() {
    }

    // Builder pattern for convenient construction
    public static Builder builder() {
        return new Builder();
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

    public String getCorporateAccountId() {
        return corporateAccountId;
    }

    public void setCorporateAccountId(String corporateAccountId) {
        this.corporateAccountId = corporateAccountId;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Integer getCompletedRequests() {
        return completedRequests;
    }

    public void setCompletedRequests(Integer completedRequests) {
        this.completedRequests = completedRequests;
    }

    public Integer getCancelledRequests() {
        return cancelledRequests;
    }

    public void setCancelledRequests(Integer cancelledRequests) {
        this.cancelledRequests = cancelledRequests;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public LocalDateTime getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDateTime lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
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

    /**
     * Builder class for CustomerResponse.
     */
    public static class Builder {
        private final CustomerResponse response = new CustomerResponse();

        public Builder id(String id) {
            response.id = id;
            return this;
        }

        public Builder userId(String userId) {
            response.userId = userId;
            return this;
        }

        public Builder customerNumber(String customerNumber) {
            response.customerNumber = customerNumber;
            return this;
        }

        public Builder firstName(String firstName) {
            response.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            response.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            response.email = email;
            return this;
        }

        public Builder primaryPhone(PhoneNumber primaryPhone) {
            response.primaryPhone = primaryPhone;
            return this;
        }

        public Builder secondaryPhone(PhoneNumber secondaryPhone) {
            response.secondaryPhone = secondaryPhone;
            return this;
        }

        public Builder address(Address address) {
            response.address = address;
            return this;
        }

        public Builder customerType(String customerType) {
            response.customerType = customerType;
            return this;
        }

        public Builder membershipLevel(String membershipLevel) {
            response.membershipLevel = membershipLevel;
            return this;
        }

        public Builder membershipExpiry(LocalDateTime membershipExpiry) {
            response.membershipExpiry = membershipExpiry;
            return this;
        }

        public Builder creditLimit(Integer creditLimit) {
            response.creditLimit = creditLimit;
            return this;
        }

        public Builder currentBalance(Integer currentBalance) {
            response.currentBalance = currentBalance;
            return this;
        }

        public Builder status(String status) {
            response.status = status;
            return this;
        }

        public Builder tenantId(String tenantId) {
            response.tenantId = tenantId;
            return this;
        }

        public Builder organizationId(String organizationId) {
            response.organizationId = organizationId;
            return this;
        }

        public Builder corporateAccountId(String corporateAccountId) {
            response.corporateAccountId = corporateAccountId;
            return this;
        }

        public Builder totalRequests(Integer totalRequests) {
            response.totalRequests = totalRequests;
            return this;
        }

        public Builder completedRequests(Integer completedRequests) {
            response.completedRequests = completedRequests;
            return this;
        }

        public Builder cancelledRequests(Integer cancelledRequests) {
            response.cancelledRequests = cancelledRequests;
            return this;
        }

        public Builder averageRating(Double averageRating) {
            response.averageRating = averageRating;
            return this;
        }

        public Builder lastServiceDate(LocalDateTime lastServiceDate) {
            response.lastServiceDate = lastServiceDate;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            response.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            response.updatedAt = updatedAt;
            return this;
        }

        public Builder createdBy(String createdBy) {
            response.createdBy = createdBy;
            return this;
        }

        public CustomerResponse build() {
            return response;
        }
    }
}
