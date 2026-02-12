package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Domain model representing SEO analysis results.
 * Contains search engine optimization metrics and recommendations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SEOAnalysis {

    private Double seoScore;
    private Integer keywordDensity;
    private String primaryKeyword;
    private List<String> extractedKeywords;
    private List<String> keyPhrases;
    private List<String> missingKeywords;

    private Boolean hasTitle;
    private Integer titleLength;
    private Boolean isTitleOptimal;
    private Boolean hasMetaDescription;
    private Integer metaDescriptionLength;
    private Boolean isMetaDescriptionOptimal;

    private Integer headingCount;
    private Boolean hasH1;
    private Integer h1Count;
    private Integer h2Count;
    private Integer h3Count;

    private Integer linkCount;
    private Integer internalLinkCount;
    private Integer externalLinkCount;
    private Boolean hasBrokenLinks;

    private Integer imageCount;
    private Integer imagesWithoutAlt;
    private Double imageOptimizationScore;

    private Integer readabilityScore;
    private Double fleschReadingEase;
    private String fleschKincaidGrade;

    private Map<String, String> seoRecommendations;
    private Map<String, Boolean> checklistItems;
    private List<String> improvementSuggestions;

    /**
     * Business logic: Check if SEO is optimized
     */
    public boolean isOptimized() {
        return this.seoScore != null && this.seoScore >= 80.0;
    }

    /**
     * Business logic: Check if title is optimal
     */
    public boolean hasOptimalTitle() {
        return this.hasTitle && this.isTitleOptimal &&
               this.titleLength != null && this.titleLength >= 30 && this.titleLength <= 60;
    }

    /**
     * Business logic: Check if meta description is optimal
     */
    public boolean hasOptimalMetaDescription() {
        return this.hasMetaDescription && this.isMetaDescriptionOptimal &&
               this.metaDescriptionLength != null && this.metaDescriptionLength >= 120 &&
               this.metaDescriptionLength <= 160;
    }

    /**
     * Business logic: Check if heading structure is correct
     */
    public boolean hasProperHeadingStructure() {
        return this.hasH1 && this.h1Count == 1 && this.headingCount > 1;
    }

    /**
     * Business logic: Get keyword density percentage
     */
    public Double getKeywordDensityPercentage() {
        if (this.keywordDensity == null) {
            return 0.0;
        }
        return this.keywordDensity / 100.0;
    }

    /**
     * Business logic: Check if keyword density is optimal
     */
    public boolean hasOptimalKeywordDensity() {
        Double density = getKeywordDensityPercentage();
        return density >= 0.01 && density <= 0.03; // 1-3%
    }

    /**
     * Business logic: Get top SEO recommendation
     */
    public String getTopRecommendation() {
        if (this.improvementSuggestions == null || this.improvementSuggestions.isEmpty()) {
            return "Content is well optimized for SEO";
        }
        return this.improvementSuggestions.get(0);
    }

    /**
     * Business logic: Calculate SEO checklist completion
     */
    public Double getChecklistCompletion() {
        if (this.checklistItems == null || this.checklistItems.isEmpty()) {
            return 0.0;
        }

        long completedCount = this.checklistItems.values().stream()
                .filter(Boolean::booleanValue)
                .count();

        return (completedCount * 100.0) / this.checklistItems.size();
    }

    /**
     * Business logic: Check if images are optimized
     */
    public boolean hasOptimizedImages() {
        return this.imageOptimizationScore != null && this.imageOptimizationScore >= 80.0 &&
               (this.imagesWithoutAlt == null || this.imagesWithoutAlt == 0);
    }
}
