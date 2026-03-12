package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a content analysis result.
 * Contains comprehensive analysis metrics for content quality, readability, sentiment, and SEO.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentAnalysis {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String contentId;
    private AnalysisStatus status;
    private String contentType;
    private String contentTitle;
    private String contentBody;
    private String contentLanguage;
    private ContentMetrics metrics;
    private SentimentAnalysis sentiment;
    private SEOAnalysis seoAnalysis;
    private ReadabilityAnalysis readability;
    private Integer wordCount;
    private Integer characterCount;
    private Integer sentenceCount;
    private Integer paragraphCount;
    private LocalDateTime analyzedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String analysisVersion;
    private Double overallScore;

    /**
     * Business logic: Calculate overall score based on all metrics
     */
    public void calculateOverallScore() {
        if (this.metrics == null) {
            this.overallScore = 0.0;
            return;
        }

        double qualityWeight = 0.30;
        double readabilityWeight = 0.25;
        double sentimentWeight = 0.20;
        double seoWeight = 0.25;

        double qualityScore = this.metrics.getQualityScore() != null ? this.metrics.getQualityScore() : 0.0;
        double readabilityScore = this.readability != null ? this.readability.getReadabilityScore() : 0.0;
        double sentimentScore = this.sentiment != null ? Math.abs(this.sentiment.getSentimentScore()) : 0.0;
        double seoScore = this.seoAnalysis != null ? this.seoAnalysis.getSeoScore() : 0.0;

        this.overallScore = (qualityScore * qualityWeight) +
                           (readabilityScore * readabilityWeight) +
                           (sentimentScore * sentimentWeight) +
                           (seoScore * seoWeight);
    }

    /**
     * Business logic: Check if analysis is completed
     */
    public boolean isCompleted() {
        return AnalysisStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if analysis is in progress
     */
    public boolean isInProgress() {
        return AnalysisStatus.IN_PROGRESS.equals(this.status);
    }

    /**
     * Business logic: Check if analysis failed
     */
    public boolean isFailed() {
        return AnalysisStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Mark as completed
     */
    public void markAsCompleted() {
        this.status = AnalysisStatus.COMPLETED;
        this.analyzedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateOverallScore();
    }

    /**
     * Business logic: Mark as failed
     */
    public void markAsFailed() {
        this.status = AnalysisStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as in progress
     */
    public void markAsInProgress() {
        this.status = AnalysisStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get quality grade (A, B, C, D, F)
     */
    public String getQualityGrade() {
        if (this.overallScore == null) {
            return "N/A";
        }

        if (this.overallScore >= 90) return "A";
        if (this.overallScore >= 80) return "B";
        if (this.overallScore >= 70) return "C";
        if (this.overallScore >= 60) return "D";
        return "F";
    }

    /**
     * Enum for analysis status
     */
    public enum AnalysisStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}
