package com.gogidix.rapidassist.ai.summarization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryQualityMetrics {
    private UUID id;
    private String tenantId;
    private UUID summaryId;
    private double overallScore;
    private double coherenceScore;
    private double consistencyScore;
    private double fluencyScore;
    private double relevanceScore;
    private Map<String, Double> additionalMetrics;
    private LocalDateTime createdAt;
}
