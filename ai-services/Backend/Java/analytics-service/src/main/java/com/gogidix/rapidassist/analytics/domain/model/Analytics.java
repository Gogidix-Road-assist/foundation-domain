package com.gogidix.rapidassist.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an analytics entry.
 * Contains aggregated data for various metrics and KPIs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analytics {

    private UUID id;
    private String tenantId;
    private String analyticsType;
    private String dataSource;
    private Map<String, Object> metrics;
    private Map<String, Object> dimensions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime computedAt;
    private String status;
    private Integer totalRecords;
    private Double aggregationValue;
    private String aggregationType;
    private String computedBy;
    private String description;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Check if analytics is expired
     */
    public boolean isExpired() {
        if (endTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(endTime.plusDays(30));
    }

    /**
     * Business logic: Get analytics age in days
     */
    public long getAgeInDays() {
        return createdAt != null ? java.time.Duration.between(createdAt, LocalDateTime.now()).toDays() : 0;
    }

    /**
     * Business logic: Check if analytics is ready for computation
     */
    public boolean isReadyForComputation() {
        return "PENDING".equals(status) && startTime != null && endTime != null;
    }

    /**
     * Business logic: Mark as computed
     */
    public void markAsComputed(Double value) {
        this.status = "COMPLETED";
        this.computedAt = LocalDateTime.now();
        this.aggregationValue = value;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as failed
     */
    public void markAsFailed(String reason) {
        this.status = "FAILED";
        this.updatedAt = LocalDateTime.now();
        if (this.metadata == null) {
            this.metadata = Map.of();
        }
        this.metadata = Map.of("failureReason", reason, "failedAt", LocalDateTime.now().toString());
    }
}
