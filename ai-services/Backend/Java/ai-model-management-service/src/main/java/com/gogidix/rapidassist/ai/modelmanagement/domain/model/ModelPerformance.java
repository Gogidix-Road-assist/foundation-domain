package com.gogidix.rapidassist.ai.modelmanagement.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing performance metrics for a model.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPerformance {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID modelId;
    private UUID modelVersionId;
    private UUID deploymentId;
    private Double accuracy;
    private Double precision;
    private Double recall;
    private Double f1Score;
    private Double auc;
    private Double latency;
    private Double throughput;
    private Double errorRate;
    private Integer requestCount;
    private Integer successCount;
    private Integer failureCount;
    private Double avgResponseTime;
    private Double p95ResponseTime;
    private Double p99ResponseTime;
    private Map<String, Double> customMetrics;
    private LocalDateTime timestamp;
    private String evaluationType;
    private Long version;
}
