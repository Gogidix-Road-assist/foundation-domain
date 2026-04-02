package com.gogidix.rapidassist.ai.contentanalysis.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for Sentiment Analysis
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysisDto {

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
