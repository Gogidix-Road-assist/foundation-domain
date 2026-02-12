package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Domain model representing content quality metrics.
 * Contains detailed measurements of content quality characteristics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentMetrics {

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

    /**
     * Business logic: Check if content meets quality threshold
     */
    public boolean meetsQualityThreshold(Double threshold) {
        return this.qualityScore != null && this.qualityScore >= threshold;
    }

    /**
     * Business logic: Get top improvement suggestion
     */
    public String getTopImprovementSuggestion() {
        if (this.improvementSuggestions == null || this.improvementSuggestions.isEmpty()) {
            return "No specific suggestions";
        }
        return this.improvementSuggestions.entrySet().stream()
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse("No specific suggestions");
    }

    /**
     * Business logic: Check if grammar is acceptable
     */
    public boolean hasAcceptableGrammar(Double minimumScore) {
        return this.grammarScore != null && this.grammarScore >= minimumScore;
    }

    /**
     * Business logic: Check if spelling is acceptable
     */
    public boolean hasAcceptableSpelling(Double minimumScore) {
        return this.spellingScore != null && this.spellingScore >= minimumScore;
    }

    /**
     * Business logic: Calculate complexity level
     */
    public String getComplexityLevel() {
        if (this.averageSentenceLength == null) {
            return "Unknown";
        }

        if (this.averageSentenceLength < 10) return "Simple";
        if (this.averageSentenceLength < 20) return "Moderate";
        if (this.averageSentenceLength < 30) return "Complex";
        return "Very Complex";
    }
}
