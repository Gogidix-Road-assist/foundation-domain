package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityMetric;
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
 * MongoDB entity for DataQualityMetric
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "data_quality_metrics")
public class DataQualityMetricEntity {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
    private String metricName;

    private String metricType;
    private String entityType;
    private Double metricValue;
    private String unit;
    @Indexed
    private LocalDateTime metricTimestamp;
    private Map<String, Object> metricDimensions;
    private Map<String, Object> metadata;
    private LocalDateTime calculatedAt;
    private String calculatedBy;
}
