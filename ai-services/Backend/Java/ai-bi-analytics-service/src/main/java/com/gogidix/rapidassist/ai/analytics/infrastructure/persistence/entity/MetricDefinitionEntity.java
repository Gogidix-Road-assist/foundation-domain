package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MongoDB entity for MetricDefinition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "metric_definitions")
public class MetricDefinitionEntity {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    @Indexed(unique = true)
    private String code;

    private String description;

    @Indexed
    private String metricType;

    private String dataSource;

    private String query;

    private Map<String, Object> configuration;

    private String unit;

    private String aggregationFunction;

    private String createdBy;

    private String updatedBy;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private Map<String, Object> metadata;

    private List<String> tags;

    @Indexed
    private Boolean isActive;

    private String category;

    private Double thresholdWarning;

    private Double thresholdCritical;

    private String formatPattern;
}
