package com.gogidix.rapidassist.ai.inference.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Inference Metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceMetricsDto {

    private UUID id;
    private UUID inferenceRequestId;
    private String tenantId;
    private String metricType;
    private String metricName;
    private Double metricValue;
    private String unit;
    private LocalDateTime timestamp;
    private Map<String, Object> labels;
}
