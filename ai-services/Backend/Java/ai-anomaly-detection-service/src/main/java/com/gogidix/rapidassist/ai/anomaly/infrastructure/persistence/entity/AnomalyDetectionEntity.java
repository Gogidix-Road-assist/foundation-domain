package com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalySeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for AnomalyDetection.
 * Maps to anomaly_detection collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "anomaly_detection")
public class AnomalyDetectionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String dataSource;

    @Indexed
    private String dataPoint;

    @Indexed
    private AnomalySeverity severity;

    @Indexed
    private AnomalyStatus status;

    private Double anomalyScore;

    private Double confidence;

    private String detectionMethod;

    private Map<String, Object> data;

    private Map<String, Object> anomalyFeatures;

    @Indexed
    private String patternId;

    @Indexed
    private String ruleId;

    @Indexed
    private LocalDateTime detectedAt;

    private LocalDateTime acknowledgedAt;

    private String acknowledgedBy;

    private LocalDateTime resolvedAt;

    private String resolvedBy;

    private String description;

    private String recommendation;

    private Map<String, Object> metadata;

    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
