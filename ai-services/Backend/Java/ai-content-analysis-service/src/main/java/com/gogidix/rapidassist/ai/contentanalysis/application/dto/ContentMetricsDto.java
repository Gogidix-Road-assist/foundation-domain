package com.gogidix.rapidassist.ai.contentanalysis.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for Content Metrics
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentMetricsDto {

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
