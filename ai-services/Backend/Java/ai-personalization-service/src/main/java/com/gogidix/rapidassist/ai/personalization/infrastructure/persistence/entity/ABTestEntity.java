package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for ABTest.
 * Maps to ab_test collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ab_test")
public class ABTestEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String testCode;

    private String testName;
    private String description;

    // Test configuration
    @Indexed
    private String testType;
    private String hypothesis;
    private Map<String, Object> variants;
    private String metricType;
    private String targetMetric;

    // Traffic allocation
    private Integer totalTrafficPercentage;
    private Map<String, Integer> trafficAllocation;
    private Integer minSampleSize;
    private Integer maxSampleSize;

    // Duration and schedule
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer durationDays;

    // Status
    @Indexed
    private String status;

    // Results
    private String winningVariant;
    private Double confidenceLevel;
    private Double statisticalSignificance;
    private Boolean isStatisticallySignificant;
    private Map<String, Object> results;
    private String conclusion;

    // Targeting
    private List<String> targetSegmentIds;
    private List<String> excludedSegmentIds;
    private Map<String, Object> targetCriteria;

    // Metadata
    private String category;
    private List<String> tags;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
