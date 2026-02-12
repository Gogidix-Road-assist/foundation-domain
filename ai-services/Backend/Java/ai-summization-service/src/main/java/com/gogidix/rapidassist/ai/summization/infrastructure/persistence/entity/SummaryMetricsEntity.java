package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for SummaryMetrics.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "summary_metrics")
public class SummaryMetricsEntity {

    @org.springframework.data.annotation.Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID documentSummaryId;

    private Double compressionRatio;

    private Integer originalLength;

    private Integer summaryLength;

    private Double relevanceScore;

    private Double coherenceScore;

    private Double fluencyScore;

    private Double overallQualityScore;

    private Integer keyPointsCount;

    private Integer sentencesCount;

    private Integer wordsCount;

    private Long processingTimeMs;

    private Double confidenceScore;

    private String language;

    private Boolean passedQualityThreshold;

    private String additionalScores;

    private LocalDateTime createdAt;

    private Long version;
}
