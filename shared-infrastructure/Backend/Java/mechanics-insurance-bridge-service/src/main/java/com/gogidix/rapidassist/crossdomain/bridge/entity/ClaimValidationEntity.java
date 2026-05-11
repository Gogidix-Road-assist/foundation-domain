package com.gogidix.rapidassist.crossdomain.bridge.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

import com.gogidix.rapidassist.shared.persistence.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for storing claim validation records.
 * Tracks validation history between Mechanics and Insurance domains.
 */
@Entity
@Table(name = "claim_validations", indexes = {
    @Index(name = "idx_claim_id", columnList = "claim_id"),
    @Index(name = "idx_policy_number", columnList = "policy_number"),
    @Index(name = "idx_workshop_id", columnList = "workshop_id"),
    @Index(name = "idx_mechanic_id", columnList = "mechanic_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_validated_at", columnList = "validated_at"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClaimValidationEntity extends BaseEntity {

    @Column(name = "claim_id", nullable = false, length = 100)
    private String claimId;

    @Column(name = "policy_number", nullable = false, length = 100)
    private String policyNumber;

    @Column(name = "customer_id", length = 100)
    private String customerId;

    @Column(name = "vehicle_registration", length = 50)
    private String vehicleRegistration;

    @Column(name = "workshop_id", length = 100)
    private String workshopId;

    @Column(name = "mechanic_id", length = 100)
    private String mechanicId;

    @Column(name = "estimated_cost", nullable = false)
    private BigDecimal estimatedCost;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Column(name = "valid", nullable = false)
    private Boolean valid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ValidationStatus status;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // Policy validation results
    @Column(name = "policy_active")
    private Boolean policyActive;

    @Column(name = "coverage_valid")
    private Boolean coverageValid;

    @Column(name = "within_limit")
    private Boolean withinLimit;

    @Column(name = "workshop_approved")
    private Boolean workshopApproved;

    @Column(name = "mechanic_certified")
    private Boolean mechanicCertified;

    @Column(name = "coverage_type", length = 50)
    private String coverageType;

    @Column(name = "coverage_limit")
    private BigDecimal coverageLimit;

    @Column(name = "remaining_limit")
    private BigDecimal remainingLimit;

    @Column(name = "deductible")
    private BigDecimal deductible;

    @Column(name = "validated_at", nullable = false)
    private LocalDateTime validatedAt;

    @Column(name = "validated_by", length = 100)
    private String validatedBy;

    @Column(name = "validation_reference", length = 100)
    private String validationReference;

    @Column(name = "booking_id", length = 100)
    private String bookingId;

    /**
     * Validation status enum
     */
    public enum ValidationStatus {
        PENDING,
        APPROVED,
        REJECTED,
        REQUIRES_INFO,
        CANCELLED
    }

    /**
     * Generate unique validation reference
     */
    public static String generateReference() {
        return "VAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Check if validation is successful
     */
    public boolean isSuccessful() {
        return valid != null && valid &&
               (status == ValidationStatus.APPPROVED || status == ValidationStatus.PENDING);
    }

    /**
     * Check if validation requires follow-up
     */
    public boolean requiresFollowUp() {
        return status == ValidationStatus.REQUIRES_INFO;
    }
}
