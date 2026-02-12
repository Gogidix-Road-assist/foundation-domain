package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Embedded entity for SentimentAnalysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysisEntity {

    private Double sentimentScore;
    private String sentimentType;
    private Double confidence;
    private Double positivityScore;
    private Double negativityScore;
    private Double neutralityScore;

    private String dominantEmotion;
    private Map<String, Double> emotionBreakdown;
    private Map<String, Double> sentimentKeywords;

    private Integer positiveWordCount;
    private Integer negativeWordCount;
    private Integer neutralWordCount;
}
