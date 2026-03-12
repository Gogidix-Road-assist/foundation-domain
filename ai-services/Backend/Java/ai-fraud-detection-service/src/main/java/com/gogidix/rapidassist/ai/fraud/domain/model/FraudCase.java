package com.gogidix.rapidassist.ai.fraud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a fraud investigation case.
 * Created when fraud alert requires detailed investigation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FraudCase {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String caseNumber;
    private String caseType;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String assignedTo;
    private String assignedTeam;
    private List<UUID> linkedAlerts;
    private List<UUID> linkedDetections;
    private String investigationSummary;
    private Map<String, Object> investigationDetails;
    private String finding;
    private String decision;
    private String actionTaken;
    private String outcome;
    private Double estimatedLoss;
    private Double recoveredAmount;
    private LocalDateTime openedDate;
    private LocalDateTime assignedDate;
    private LocalDateTime startDate;
    private LocalDateTime closedDate;
    private String closedBy;
    private String closureReason;
    private Integer durationDays;
    private String createdBy;
    private String updatedBy;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Open case
     */
    public void open(String caseType, String assignedTo) {
        this.status = "OPEN";
        this.caseType = caseType;
        this.assignedTo = assignedTo;
        this.openedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Start investigation
     */
    public void startInvestigation() {
        this.status = "IN_PROGRESS";
        this.startDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Close case
     */
    public void close(String closedBy, String closureReason, String outcome) {
        this.status = "CLOSED";
        this.closedBy = closedBy;
        this.closureReason = closureReason;
        this.outcome = outcome;
        this.closedDate = LocalDateTime.now();
        if (this.startDate != null) {
            this.durationDays = (int) java.time.Duration.between(this.startDate, this.closedDate).toDays();
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Assign to investigator
     */
    public void assignTo(String investigatorId) {
        this.assignedTo = investigatorId;
        this.assignedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update investigation findings
     */
    public void updateFindings(String summary, String finding, String decision) {
        this.investigationSummary = summary;
        this.finding = finding;
        this.decision = decision;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Record action taken
     */
    public void recordAction(String action, Double recoveredAmount) {
        this.actionTaken = action;
        this.recoveredAmount = recoveredAmount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if case is open
     */
    public boolean isOpen() {
        return "OPEN".equalsIgnoreCase(status) || "IN_PROGRESS".equalsIgnoreCase(status);
    }

    /**
     * Business logic: Check if case is high priority
     */
    public boolean isHighPriority() {
        return "HIGH".equalsIgnoreCase(priority) || "CRITICAL".equalsIgnoreCase(priority);
    }

    /**
     * Business logic: Calculate case age in days
     */
    public long getAgeInDays() {
        return openedDate != null ? java.time.Duration.between(openedDate, LocalDateTime.now()).toDays() : 0;
    }
}
