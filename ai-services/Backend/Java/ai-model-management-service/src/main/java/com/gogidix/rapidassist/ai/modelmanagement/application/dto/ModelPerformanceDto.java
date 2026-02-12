package com.gogidix.rapidassist.ai.modelmanagement.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing performance metrics for a model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPerformanceDto {

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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String evaluationType;
    private Long version;
}
