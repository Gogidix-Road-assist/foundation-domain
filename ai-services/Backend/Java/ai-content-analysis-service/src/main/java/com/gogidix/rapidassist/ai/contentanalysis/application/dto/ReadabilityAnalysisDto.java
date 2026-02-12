package com.gogidix.rapidassist.ai.contentanalysis.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for Readability Analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadabilityAnalysisDto {

    private Double readabilityScore;
    private String readabilityLevel;
    private String targetAudience;
    private Double fleschReadingEase;
    private String fleschKincaidGrade;
    private Double gunningFogIndex;
    private Double colemanLiauIndex;
    private Double automatedReadabilityIndex;

    private Integer averageWordsPerSentence;
    private Integer averageSyllablesPerWord;
    private Integer averageCharactersPerWord;
    private Integer percentageOfComplexWords;
    private Integer sentenceCount;
    private Integer wordCount;
    private Integer complexWordCount;
    private Integer syllableCount;

    private String readingTimeMinutes;
    private String speakingTimeMinutes;
    private Integer estimatedPages;

    private Map<String, Double> gradeLevelBreakdown;
    private Map<String, String> difficultyFactors;
    private List<String> complexWords;
    private List<String> difficultSentences;
}
