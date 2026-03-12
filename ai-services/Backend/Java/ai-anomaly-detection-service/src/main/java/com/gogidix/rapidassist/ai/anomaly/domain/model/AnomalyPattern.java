package com.gogidix.rapidassist.ai.anomaly.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a pattern for anomaly detection.
 * Defines characteristics and rules that constitute an anomalous pattern.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnomalyPattern {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private PatternType patternType;
    private String category;
    private Map<String, Object> patternDefinition;
    private Double threshold;
    private String condition;
    private String dataSource;
    private boolean isActive;
    private Double weight;
    private Long version;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Activate pattern
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Deactivate pattern
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if pattern is active
     */
    public boolean isActivePattern() {
        return this.isActive;
    }

    /**
     * Business logic: Update pattern definition
     */
    public void updatePattern(Map<String, Object> newDefinition) {
        this.patternDefinition = newDefinition;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update threshold
     */
    public void updateThreshold(Double newThreshold) {
        this.threshold = newThreshold;
        this.updatedAt = LocalDateTime.now();
    }
}
