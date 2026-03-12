package com.gogidix.rapidassist.ai.dataquality.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain Model representing a data quality issue detected during checks.
 * Captures details about specific data quality problems.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityIssue {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID checkId;
    private UUID ruleId;
    private String issueType;
    private IssueSeverity severity;
    private String entityType;
    private String entityId;
    private String attributeName;
    private String currentValue;
    private String expectedValue;
    private String description;
    private IssueStatus status;
    private LocalDateTime detectedAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;
    private Map<String, Object> issueDetails;
    private int occurrenceCount;
    private String affectedBusinessKey;

    public enum IssueSeverity {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW
    }

    public enum IssueStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
        IGNORED,
        FALSE_POSITIVE
    }

    /**
     * Business logic: Mark issue as resolved
     */
    public void resolve(String resolvedBy, String resolutionNotes) {
        this.status = IssueStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = resolvedBy;
        this.resolutionNotes = resolutionNotes;
    }

    /**
     * Business logic: Mark issue as in progress
     */
    public void markInProgress() {
        this.status = IssueStatus.IN_PROGRESS;
    }

    /**
     * Business logic: Ignore issue
     */
    public void ignore() {
        this.status = IssueStatus.IGNORED;
    }

    /**
     * Business logic: Mark as false positive
     */
    public void markAsFalsePositive() {
        this.status = IssueStatus.FALSE_POSITIVE;
    }

    /**
     * Business logic: Increment occurrence count
     */
    public void incrementOccurrence() {
        this.occurrenceCount++;
    }

    /**
     * Business logic: Check if issue is resolved
     */
    public boolean isResolved() {
        return this.status == IssueStatus.RESOLVED;
    }

    /**
     * Business logic: Check if issue is open
     */
    public boolean isOpen() {
        return this.status == IssueStatus.OPEN;
    }

    /**
     * Business logic: Check if issue is critical
     */
    public boolean isCritical() {
        return this.severity == IssueSeverity.CRITICAL;
    }

    /**
     * Business logic: Get age of issue in days
     */
    public long getAgeInDays() {
        if (detectedAt == null) {
            return 0;
        }
        LocalDateTime endDateTime = resolvedAt != null ? resolvedAt : LocalDateTime.now();
        return java.time.Duration.between(detectedAt, endDateTime).toDays();
    }

    /**
     * Business logic: Check if issue is stale (older than specified days)
     */
    public boolean isStale(int staleThresholdDays) {
        return getAgeInDays() > staleThresholdDays && !isResolved();
    }
}
