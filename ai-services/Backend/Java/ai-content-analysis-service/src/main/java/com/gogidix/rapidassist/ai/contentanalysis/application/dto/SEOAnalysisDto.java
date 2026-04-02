package com.gogidix.rapidassist.ai.contentanalysis.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for SEO Analysis
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SEOAnalysisDto {

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
}
