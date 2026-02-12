package com.gogidix.rapidassist.ai.translation.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a translation request.
 * Contains source text, target language, and translation result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRequest {

    private UUID id;
    private String tenantId;
    private UUID translationSessionId;

    /**
     * Source text to translate.
     */
    private String sourceText;

    /**
     * Source language code (ISO 639-1).
     */
    private String sourceLanguage;

    /**
     * Target language code (ISO 639-1).
     */
    private String targetLanguage;

    /**
     * Translated text (result).
     */
    private String translatedText;

    /**
     * Translation status.
     */
    @Builder.Default
    private TranslationStatus status = TranslationStatus.PENDING;

    /**
     * Quality metrics.
     */
    private TranslationQuality quality;

    /**
     * Detected language (if source language not specified).
     */
    private String detectedLanguage;

    /**
     * Confidence score for language detection (0.0 to 1.0).
     */
    private Double detectionConfidence;

    /**
     * Processing time in milliseconds.
     */
    private Long processingTimeMs;

    /**
     * Whether translation was retrieved from cache.
     */
    @Builder.Default
    private Boolean fromCache = false;

    /**
     * Cache key used.
     */
    private String cacheKey;

    /**
     * Model version used for translation.
     */
    private String modelVersion;

    /**
     * Additional metadata.
     */
    private java.util.Map<String, Object> metadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    /**
     * Business logic: Create a new translation request.
     */
    public static TranslationRequest create(String tenantId, UUID translationSessionId,
                                             String sourceText, String sourceLanguage, String targetLanguage) {
        return TranslationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .translationSessionId(translationSessionId)
                .sourceText(sourceText)
                .sourceLanguage(sourceLanguage)
                .targetLanguage(targetLanguage)
                .status(TranslationStatus.PENDING)
                .fromCache(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Mark translation as in progress.
     */
    public void markInProgress() {
        this.status = TranslationStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Complete translation with result.
     */
    public void complete(String translatedText, TranslationQuality quality, Long processingTimeMs, String modelVersion) {
        this.translatedText = translatedText;
        this.quality = quality;
        this.processingTimeMs = processingTimeMs;
        this.modelVersion = modelVersion;
        this.status = TranslationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Complete translation from cache.
     */
    public void completeFromCache(String translatedText, TranslationQuality quality, String cacheKey) {
        this.translatedText = translatedText;
        this.quality = quality;
        this.cacheKey = cacheKey;
        this.fromCache = true;
        this.status = TranslationStatus.CACHED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark translation as failed.
     */
    public void fail(String errorMessage) {
        this.status = TranslationStatus.FAILED;
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put("errorMessage", errorMessage);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Cancel translation.
     */
    public void cancel() {
        this.status = TranslationStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Set detected language.
     */
    public void setDetectedLanguage(String language, Double confidence) {
        this.detectedLanguage = language;
        this.detectionConfidence = confidence;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if translation is completed.
     */
    public boolean isCompleted() {
        return TranslationStatus.COMPLETED.equals(this.status) || TranslationStatus.CACHED.equals(this.status);
    }

    /**
     * Business logic: Check if translation is failed.
     */
    public boolean isFailed() {
        return TranslationStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Check if translation is in progress.
     */
    public boolean isInProgress() {
        return TranslationStatus.IN_PROGRESS.equals(this.status);
    }

    /**
     * Business logic: Check if translation quality is acceptable.
     */
    public boolean hasAcceptableQuality() {
        return quality != null && quality.isAcceptable();
    }

    /**
     * Business logic: Get language pair code.
     */
    public String getLanguagePairCode() {
        String source = sourceLanguage != null ? sourceLanguage : detectedLanguage;
        return source + "-" + targetLanguage;
    }

    /**
     * Business logic: Calculate text length (source).
     */
    public int getSourceTextLength() {
        return sourceText != null ? sourceText.length() : 0;
    }

    /**
     * Business logic: Calculate text length (translated).
     */
    public int getTranslatedTextLength() {
        return translatedText != null ? translatedText.length() : 0;
    }

    /**
     * Business logic: Check if translation speed is fast (< 1 second).
     */
    public boolean isFastTranslation() {
        return processingTimeMs != null && processingTimeMs < 1000L;
    }
}
