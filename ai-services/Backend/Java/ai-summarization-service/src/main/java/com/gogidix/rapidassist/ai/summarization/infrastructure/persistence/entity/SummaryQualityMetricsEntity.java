package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity;

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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "summary_quality_metrics")
public class SummaryQualityMetricsEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID summaryId;

    private double overallScore;

    private double coherenceScore;

    private double consistencyScore;

    private double fluencyScore;

    private double relevanceScore;

    private Map<String, Double> additionalMetrics;

    private LocalDateTime createdAt;
}
