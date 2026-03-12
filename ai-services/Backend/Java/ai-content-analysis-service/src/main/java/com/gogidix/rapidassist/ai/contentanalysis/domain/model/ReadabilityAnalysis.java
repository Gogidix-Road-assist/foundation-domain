package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Domain model representing readability analysis results.
 * Contains metrics assessing how easy content is to read and understand.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadabilityAnalysis {

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

    /**
     * Business logic: Check if content is easy to read
     */
    public boolean isEasyToRead() {
        return this.readabilityScore != null && this.readabilityScore >= 80.0;
    }

    /**
     * Business logic: Check if content requires college education
     */
    public boolean requiresCollegeLevel() {
        if (this.fleschKincaidGrade == null) {
            return false;
        }
        try {
            double grade = Double.parseDouble(this.fleschKincaidGrade);
            return grade >= 13.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Business logic: Get appropriate age group
     */
    public String getAppropriateAgeGroup() {
        if (this.fleschKincaidGrade == null) {
            return "Unknown";
        }

        try {
            double grade = Double.parseDouble(this.fleschKincaidGrade);
            if (grade <= 5) return "Elementary School (Ages 5-10)";
            if (grade <= 8) return "Middle School (Ages 11-13)";
            if (grade <= 12) return "High School (Ages 14-18)";
            return "College/Adult (Ages 18+)";
        } catch (NumberFormatException e) {
            return "Unknown";
        }
    }

    /**
     * Business logic: Get reading difficulty description
     */
    public String getDifficultyDescription() {
        if (this.fleschReadingEase == null) {
            return "Unknown";
        }

        double score = this.fleschReadingEase;
        if (score >= 90) return "Very Easy (5th grade)";
        if (score >= 80) return "Easy (6th grade)";
        if (score >= 70) return "Fairly Easy (7th grade)";
        if (score >= 60) return "Standard (8th-9th grade)";
        if (score >= 50) return "Fairly Difficult (10th-12th grade)";
        if (score >= 30) return "Difficult (College)";
        return "Very Difficult (Professional)";
    }

    /**
     * Business logic: Check if content is appropriate for target audience
     */
    public boolean isAppropriateForAudience(String targetAudience) {
        if (this.targetAudience == null || targetAudience == null) {
            return true;
        }
        return this.targetAudience.equalsIgnoreCase(targetAudience);
    }

    /**
     * Business logic: Get complexity percentage
     */
    public Double getComplexityPercentage() {
        if (this.percentageOfComplexWords == null) {
            return 0.0;
        }
        return this.percentageOfComplexWords / 100.0;
    }

    /**
     * Business logic: Check if content has too many complex words
     */
    public boolean hasExcessiveComplexWords(Double threshold) {
        return getComplexityPercentage() > threshold;
    }
}
