package com.gogidix.rapidassist.ai.modelmanagement.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an A/B test for model comparison.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ABTest {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private UUID controlModelVersionId;
    private UUID treatmentModelVersionId;
    private Double trafficSplitPercentage;
    private ABTestStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long totalParticipants;
    private Long controlParticipants;
    private Long treatmentParticipants;
    private Map<String, Double> controlMetrics;
    private Map<String, Double> treatmentMetrics;
    private String winner;
    private Double statisticalSignificance;
    private Map<String, Object> metadata;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
