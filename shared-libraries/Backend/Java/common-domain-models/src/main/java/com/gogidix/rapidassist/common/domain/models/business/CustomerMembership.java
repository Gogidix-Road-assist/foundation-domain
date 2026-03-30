package com.gogidix.rapidassist.common.domain.models.business;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * CustomerMembership entity linking customers to membership programs.
 */
@Document(collection = "customer_membership")
public class CustomerMembership {

    @Id
        private String id;

            private Customer customer;

        private String membershipProgramId;

        private String membershipLevel;

        private LocalDateTime startDate;

        private LocalDateTime endDate;

        private Boolean autoRenew = false;

        private String status = "ACTIVE"; // ACTIVE, EXPIRED, CANCELLED, SUSPENDED

        private String benefitsJson;

        private LocalDateTime createdAt;

    public CustomerMembership() {
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getMembershipProgramId() {
        return membershipProgramId;
    }

    public void setMembershipProgramId(String membershipProgramId) {
        this.membershipProgramId = membershipProgramId;
    }

    public String getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBenefitsJson() {
        return benefitsJson;
    }

    public void setBenefitsJson(String benefitsJson) {
        this.benefitsJson = benefitsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status) &&
               (endDate == null || endDate.isAfter(LocalDateTime.now()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerMembership)) return false;
        CustomerMembership that = (CustomerMembership) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
