package com.gogidix.rapidassist.ai.anomaly.application.dto;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalySeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for AnomalyDetection
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyDetectionDto {

    private UUID id;
    private String tenantId;
    private String dataSource;
    private String dataPoint;
    private AnomalySeverity severity;
    private AnomalyStatus status;
    private Double anomalyScore;
    private Double confidence;
    private String detectionMethod;
    private Map<String, Object> data;
    private Map<String, Object> anomalyFeatures;
    private String patternId;
    private String ruleId;
    private LocalDateTime detectedAt;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String description;
    private String recommendation;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
