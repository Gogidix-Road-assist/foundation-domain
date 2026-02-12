package com.gogidix.rapidassist.ai.anomaly.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a detection rule for anomalies.
 * Contains the logic and conditions for detecting specific types of anomalies.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionRule {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private RuleType ruleType;
    private List<String> patternIds;
    private Map<String, Object> conditions;
    private String dataSource;
    private Integer priority;
    private Boolean isActive;
    private Boolean createAlert;
    private AlertSeverity alertSeverity;
    private List<String> notificationChannels;
    private String category;
    private Map<String, Object> metadata;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    /**
     * Business logic: Activate rule
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Deactivate rule
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if rule is active
     */
    public boolean isActiveRule() {
        return this.isActive;
    }

    /**
     * Business logic: Update conditions
     */
    public void updateConditions(Map<String, Object> newConditions) {
        this.conditions = newConditions;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update priority
     */
    public void updatePriority(Integer newPriority) {
        this.priority = newPriority;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if should create alert
     */
    public boolean shouldCreateAlert() {
        return this.isActive && Boolean.TRUE.equals(this.createAlert);
    }

    /**
     * Business logic: Check if has high priority
     */
    public boolean isHighPriority() {
        return this.priority != null && this.priority >= 8;
    }
}
