package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity;

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
 * MongoDB Entity for ModelPerformance.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "model_performance")
public class ModelPerformanceEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID modelId;

    @Indexed
    private UUID modelVersionId;

    @Indexed
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
