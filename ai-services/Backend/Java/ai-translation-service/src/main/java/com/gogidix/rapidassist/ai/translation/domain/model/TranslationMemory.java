package com.gogidix.rapidassist.ai.translation.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing translation memory.
 * Stores previously translated segments for reuse and consistency.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationMemory {

    private UUID id;
    private String tenantId;

    /**
     * Source text segment.
     */
    private String sourceSegment;

    /**
     * Source language code.
     */
    private String sourceLanguage;

    /**
     * Target text segment (translation).
     */
    private String targetSegment;

    /**
     * Target language code.
     */
    private String targetLanguage;

    /**
     * Usage count (how many times this translation was reused).
     */
    @Builder.Default
    private Integer usageCount = 0;

    /**
     * Quality score (0.0 to 1.0).
     */
    private Double qualityScore;

    /**
     * Last used date.
     */
    private LocalDateTime lastUsedAt;

    /**
     * Context/domain information.
     */
    private String context;

    /**
     * Domain/industry (e.g., "medical", "legal").
     */
    private String domain;

    /**
     * Whether this translation is verified by human.
     */
    @Builder.Default
    private Boolean verified = false;

    /**
     * Verified by (user ID).
     */
    private String verifiedBy;

    /**
     * Verified at date.
     */
    private LocalDateTime verifiedAt;

    /**
     * Hash of source segment for quick lookup.
     */
    private String sourceHash;

    /**
     * Additional metadata.
     */
    private java.util.Map<String, Object> metadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Create new translation memory entry.
     */
    public static TranslationMemory create(String tenantId, String sourceSegment, String sourceLanguage,
                                            String targetSegment, String targetLanguage, String context) {
        String sourceHash = generateHash(sourceSegment, sourceLanguage, targetLanguage);

        return TranslationMemory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .sourceSegment(sourceSegment)
                .sourceLanguage(sourceLanguage)
                .targetSegment(targetSegment)
                .targetLanguage(targetLanguage)
                .usageCount(0)
                .qualityScore(0.8)
                .context(context)
                .verified(false)
                .sourceHash(sourceHash)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Generate hash for source segment.
     */
    private static String generateHash(String sourceSegment, String sourceLanguage, String targetLanguage) {
        String input = sourceLanguage + ":" + targetLanguage + ":" + sourceSegment;
        return UUID.nameUUIDFromBytes(input.getBytes()).toString();
    }

    /**
     * Business logic: Record usage.
     */
    public void recordUsage() {
        this.usageCount++;
        this.lastUsedAt = LocalDateTime.now();
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
     * Business logic: Mark as verified.
     */
    public void markAsVerified(String verifiedBy) {
        this.verified = true;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = LocalDateTime.now();
        this.qualityScore = 1.0; // Verified translations get perfect score
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update translation.
     */
    public void updateTranslation(String newTargetSegment, String updatedBy) {
        this.targetSegment = newTargetSegment;
        this.verified = false; // Reset verification after update
        this.updatedAt = LocalDateTime.now();
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put("lastUpdatedBy", updatedBy);
    }

    /**
     * Business logic: Get language pair code.
     */
    public String getLanguagePairCode() {
        return sourceLanguage + "-" + targetLanguage;
    }

    /**
     * Business logic: Check if translation is verified.
     */
    public boolean isVerified() {
        return Boolean.TRUE.equals(this.verified);
    }

    /**
     * Business logic: Check if quality is high.
     */
    public boolean isHighQuality() {
        return qualityScore != null && qualityScore >= 0.8;
    }

    /**
     * Business logic: Check if frequently used.
     */
    public boolean isFrequentlyUsed() {
        return usageCount != null && usageCount >= 10;
    }

    /**
     * Business logic: Calculate similarity with source segment.
     * Simple implementation using Levenshtein distance.
     */
    public Double calculateSimilarity(String otherSourceSegment) {
        if (sourceSegment == null || otherSourceSegment == null) {
            return 0.0;
        }

        int distance = levenshteinDistance(sourceSegment, otherSourceSegment);
        int maxLength = Math.max(sourceSegment.length(), otherSourceSegment.length());

        if (maxLength == 0) {
            return 1.0;
        }

        return 1.0 - ((double) distance / maxLength);
    }

    /**
     * Calculate Levenshtein distance between two strings.
     */
    private int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }

        return dp[len1][len2];
    }

    /**
     * Business logic: Check if stale (not used in specified days).
     */
    public boolean isStale(int staleThresholdDays) {
        if (lastUsedAt == null) {
            return true;
        }
        long daysSinceLastUse = java.time.temporal.ChronoUnit.DAYS.between(lastUsedAt, LocalDateTime.now());
        return daysSinceLastUse > staleThresholdDays;
    }
}
