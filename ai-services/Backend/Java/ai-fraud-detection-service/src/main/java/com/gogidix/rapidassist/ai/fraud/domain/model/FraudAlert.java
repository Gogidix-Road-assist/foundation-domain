package com.gogidix.rapidassist.ai.fraud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a fraud alert.
 * Generated when fraud is detected and requires attention.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlert {

    private UUID id;
    private String tenantId;
    private UUID fraudDetectionId;
    private String alertType;
    private String severity;
    private String title;
    private String description;
    private Map<String, Object> alertDetails;
    private String status;
    private String assignedTo;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    private String acknowledgmentNotes;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;
    private UUID fraudCaseId;
    private Boolean caseCreated;
    private Integer escalationLevel;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Acknowledge alert
     */
    public void acknowledge(String acknowledgedBy, String notes) {
        this.status = "ACKNOWLEDGED";
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgmentNotes = notes;
        this.acknowledgedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Resolve alert
     */
    public void resolve(String resolvedBy, String notes) {
        this.status = "RESOLVED";
        this.resolvedBy = resolvedBy;
        this.resolutionNotes = notes;
        this.resolvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Escalate alert
     */
    public void escalate() {
        this.escalationLevel = this.escalationLevel == null ? 1 : this.escalationLevel + 1;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Link to fraud case
     */
    public void linkToCase(UUID caseId) {
        this.fraudCaseId = caseId;
        this.caseCreated = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if alert is critical
     */
    public boolean isCritical() {
        return "CRITICAL".equalsIgnoreCase(severity) ||
               (escalationLevel != null && escalationLevel >= 3);
    }

    /**
     * Business logic: Check if alert is pending
     */
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status) || "OPEN".equalsIgnoreCase(status);
    }

    /**
     * Business logic: Calculate alert age in hours
     */
    public long getAgeInHours() {
        return createdAt != null ? java.time.Duration.between(createdAt, LocalDateTime.now()).toHours() : 0;
    }
}
