package com.gogidix.rapidassist.ai.inference.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing inference metrics
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inference_metrics")
public class InferenceMetrics {

    @Id
    @EqualsAndHashCode.Include

    private UUID id;

    @Indexed
    private UUID inferenceRequestId;

    @Indexed
    private String tenantId;

    private String metricType;
    private String metricName;
    private Double metricValue;
    private String unit;
    private LocalDateTime timestamp;
    private java.util.Map<String, Object> labels;

    public static InferenceMetrics create(String tenantId, String metricType,
                                         String metricName, Double metricValue, String unit) {
        return InferenceMetrics.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .metricType(metricType)
                .metricName(metricName)
                .metricValue(metricValue)
                .unit(unit)
                .timestamp(LocalDateTime.now())
                .labels(new java.util.HashMap<>())
                .build();
    }

    public void addLabel(String key, Object value) {
        if (this.labels == null) {
            this.labels = new java.util.HashMap<>();
        }
        this.labels.put(key, value);
    }
}
