package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Embedded entity for ContentMetrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentMetricsEntity {

    private Double qualityScore;
    private Double engagementScore;
    private Double clarityScore;
    private Double coherenceScore;
    private Double originalityScore;
    private Double formattingScore;
    private Double grammarScore;
    private Double spellingScore;
    private Double structureScore;
    private Double completenessScore;

    private Integer uniqueWordCount;
    private Integer averageWordLength;
    private Integer averageSentenceLength;
    private Integer averageParagraphLength;
    private Double vocabularyRichness;
    private Double lexicalDiversity;

    private Map<String, Double> additionalMetrics;
    private Map<String, String> qualityFlags;
    private Map<String, String> improvementSuggestions;
}
