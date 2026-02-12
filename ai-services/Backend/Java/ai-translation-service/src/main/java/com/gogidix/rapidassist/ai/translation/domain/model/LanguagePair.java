package com.gogidix.rapidassist.ai.translation.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a language pair for translation.
 * Tracks supported source and target language combinations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguagePair {

    private UUID id;
    private String tenantId;

    /**
     * Source language code (ISO 639-1, e.g., "en").
     */
    private String sourceLanguage;

    /**
     * Source language name (e.g., "English").
     */
    private String sourceLanguageName;

    /**
     * Target language code (ISO 639-1, e.g., "es").
     */
    private String targetLanguage;

    /**
     * Target language name (e.g., "Spanish").
     */
    private String targetLanguageName;

    /**
     * Whether this language pair is supported.
     */
    @Builder.Default
    private Boolean supported = true;

    /**
     * Quality score for this language pair (0.0 to 1.0).
     */
    private Double qualityScore;

    /**
     * Average processing time in milliseconds.
     */
    private Long averageProcessingTimeMs;

    /**
     * Total translation count for this pair.
     */
    @Builder.Default
    private Long translationCount = 0L;

    /**
     * Whether this pair requires specialized model.
     */
    @Builder.Default
    private Boolean requiresSpecializedModel = false;

    /**
     * Model version to use for this pair.
     */
    private String modelVersion;

    /**
     * Additional configuration for this language pair.
     */
    private java.util.Map<String, Object> configuration;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Create a new language pair.
     */
    public static LanguagePair create(String tenantId, String sourceLanguage, String sourceLanguageName,
                                       String targetLanguage, String targetLanguageName) {
        return LanguagePair.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .sourceLanguage(sourceLanguage)
                .sourceLanguageName(sourceLanguageName)
                .targetLanguage(targetLanguage)
                .targetLanguageName(targetLanguageName)
                .supported(true)
                .qualityScore(0.8)
                .translationCount(0L)
                .requiresSpecializedModel(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Get language pair code (e.g., "en-es").
     */
    public String getLanguagePairCode() {
        return sourceLanguage + "-" + targetLanguage;
    }

    /**
     * Business logic: Increment translation count.
     */
    public void incrementTranslationCount() {
        this.translationCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update quality score.
     */
    public void updateQualityScore(Double newScore) {
        if (this.qualityScore == null) {
            this.qualityScore = newScore;
        } else {
            // Weighted average (70% old, 30% new)
            this.qualityScore = (this.qualityScore * 0.7) + (newScore * 0.3);
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update average processing time.
     */
    public void updateAverageProcessingTime(Long processingTimeMs) {
        if (this.averageProcessingTimeMs == null) {
            this.averageProcessingTimeMs = processingTimeMs;
        } else {
            // Exponential moving average
            this.averageProcessingTimeMs = (long) ((this.averageProcessingTimeMs * 0.8) + (processingTimeMs * 0.2));
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if language pair is high quality.
     */
    public boolean isHighQuality() {
        return qualityScore != null && qualityScore >= 0.8;
    }

    /**
     * Business logic: Check if processing is fast.
     */
    public boolean isFastProcessing() {
        return averageProcessingTimeMs != null && averageProcessingTimeMs < 1000L;
    }

    /**
     * Business logic: Disable language pair.
     */
    public void disable() {
        this.supported = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Enable language pair.
     */
    public void enable() {
        this.supported = true;
        this.updatedAt = LocalDateTime.now();
    }
}
