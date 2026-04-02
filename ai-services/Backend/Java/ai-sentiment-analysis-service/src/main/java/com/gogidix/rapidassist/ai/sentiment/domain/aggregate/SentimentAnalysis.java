package com.gogidix.rapidassist.ai.sentiment.domain.aggregate;

import com.gogidix.rapidassist.ai.sentiment.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Sentiment Analysis.
 * Manages the lifecycle and business logic of sentiment analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysis {

    private UUID id;
    private String tenantId;
    private String userId;
    private String sourceType;
    private String sourceId;
    private String text;
    private String language;
    private AnalysisStatus status;
    private SentimentType overallSentiment;
    private SentimentCategory sentimentCategory;
    private Double sentimentScore;
    private Double confidence;
    private List<Emotion> emotions;
    private List<Aspect> aspects;
    private Integer wordCount;
    private Integer characterCount;
    private String errorMessage;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime analyzedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Initialize a new sentiment analysis request
     */
    public static SentimentAnalysis initialize(String tenantId, String userId, String text, String sourceType, String sourceId) {
        return SentimentAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .text(text)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .status(AnalysisStatus.PENDING)
                .language("en")
                .emotions(new ArrayList<>())
                .aspects(new ArrayList<>())
                .wordCount(text != null ? text.split("\\s+").length : 0)
                .characterCount(text != null ? text.length() : 0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Mark as processing
     */
    public void markAsProcessing() {
        if (this.status != AnalysisStatus.PENDING) {
            throw new IllegalStateException("Cannot start processing from status: " + this.status);
        }
        this.status = AnalysisStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as completed with results
     */
    public void markAsCompleted(SentimentType sentiment, SentimentCategory category, Double score, Double confidence) {
        if (this.status != AnalysisStatus.PROCESSING) {
            throw new IllegalStateException("Cannot complete from status: " + this.status);
        }
        this.status = AnalysisStatus.COMPLETED;
        this.overallSentiment = sentiment;
        this.sentimentCategory = category;
        this.sentimentScore = score;
        this.confidence = confidence;
        this.analyzedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = AnalysisStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add emotion to analysis
     */
    public void addEmotion(Emotion emotion) {
        if (this.emotions == null) {
            this.emotions = new ArrayList<>();
        }
        this.emotions.add(emotion);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add aspect to analysis
     */
    public void addAspect(Aspect aspect) {
        if (this.aspects == null) {
            this.aspects = new ArrayList<>();
        }
        this.aspects.add(aspect);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if analysis is completed
     */
    public boolean isCompleted() {
        return AnalysisStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if analysis is failed
     */
    public boolean isFailed() {
        return AnalysisStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Check if analysis is processing
     */
    public boolean isProcessing() {
        return AnalysisStatus.PROCESSING.equals(this.status);
    }

    /**
     * Business logic: Check if sentiment is positive
     */
    public boolean isPositive() {
        return SentimentType.POSITIVE.equals(this.overallSentiment);
    }

    /**
     * Business logic: Check if sentiment is negative
     */
    public boolean isNegative() {
        return SentimentType.NEGATIVE.equals(this.overallSentiment);
    }

    /**
     * Business logic: Check if sentiment is neutral
     */
    public boolean isNeutral() {
        return SentimentType.NEUTRAL.equals(this.overallSentiment);
    }

    /**
     * Business logic: Check if sentiment is mixed
     */
    public boolean isMixed() {
        return SentimentType.MIXED.equals(this.overallSentiment);
    }

    /**
     * Business logic: Get dominant emotion
     */
    public Emotion getDominantEmotion() {
        if (this.emotions == null || this.emotions.isEmpty()) {
            return null;
        }
        return this.emotions.stream()
                .max((e1, e2) -> Double.compare(e1.getIntensity(), e2.getIntensity()))
                .orElse(null);
    }

    /**
     * Business logic: Get emotions by type
     */
    public List<Emotion> getEmotionsByType(EmotionType emotionType) {
        if (this.emotions == null) {
            return new ArrayList<>();
        }
        return this.emotions.stream()
                .filter(e -> emotionType.equals(e.getEmotionType()))
                .toList();
    }

    /**
     * Business logic: Get positive aspects
     */
    public List<Aspect> getPositiveAspects() {
        if (this.aspects == null) {
            return new ArrayList<>();
        }
        return this.aspects.stream()
                .filter(Aspect::isPositive)
                .toList();
    }

    /**
     * Business logic: Get negative aspects
     */
    public List<Aspect> getNegativeAspects() {
        if (this.aspects == null) {
            return new ArrayList<>();
        }
        return this.aspects.stream()
                .filter(Aspect::isNegative)
                .toList();
    }

    /**
     * Business logic: Get neutral aspects
     */
    public List<Aspect> getNeutralAspects() {
        if (this.aspects == null) {
            return new ArrayList<>();
        }
        return this.aspects.stream()
                .filter(Aspect::isNeutral)
                .toList();
    }

    /**
     * Business logic: Update metadata
     */
    public void updateMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Calculate analysis duration in seconds
     */
    public long getAnalysisDurationSeconds() {
        if (createdAt == null || analyzedAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, analyzedAt).getSeconds();
    }
}
