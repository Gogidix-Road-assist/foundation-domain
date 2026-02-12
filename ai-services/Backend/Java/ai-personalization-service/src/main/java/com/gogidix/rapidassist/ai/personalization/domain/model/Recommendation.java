package com.gogidix.rapidassist.ai.personalization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a personalized recommendation.
 * Generated based on user profile, preferences, and personalization rules.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {

    private UUID id;
    private String tenantId;
    private String userId;
    private UUID userProfileId;
    private String segmentId;

    // Recommendation content
    private String recommendationType;
    private String contentId;
    private String contentType;
    private String title;
    private String description;
    private String imageUrl;
    private String actionUrl;
    private String actionType;

    // Scoring and ranking
    private Double relevanceScore;
    private Double confidenceScore;
    @Builder.Default
    private Integer rank = 0;

    // Context and reasoning
    private String reason;
    private List<String> reasons;
    private Map<String, Object> context;
    private String ruleId;

    // Display settings
    private String displayLocation;
    private String displayTemplate;
    private Integer displayPriority;
    private Map<String, Object> displayParameters;

    // Status
    @Builder.Default
    private RecommendationStatus status = RecommendationStatus.PENDING;

    // Effectiveness tracking
    private Boolean wasShown;
    private Boolean wasClicked;
    private LocalDateTime shownAt;
    private LocalDateTime clickedAt;
    private Long timeToClickMs;
    private String conversionType;
    private Double conversionValue;

    // Validity
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean hasExpired;

    // Metadata
    private String campaignId;
    private String abTestId;
    private String source;
    private String algorithm;
    private String algorithmVersion;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;

    /**
     * Check if recommendation is valid and can be shown
     */
    public boolean isValid() {
        if (status != RecommendationStatus.PENDING && status != RecommendationStatus.APPROVED) {
            return false;
        }

        if (hasExpired != null && hasExpired) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }

        if (validUntil != null && now.isAfter(validUntil)) {
            return false;
        }

        return true;
    }

    /**
     * Mark as shown
     */
    public void markAsShown() {
        this.wasShown = true;
        this.shownAt = LocalDateTime.now();
        this.status = RecommendationStatus.SHOWN;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark as clicked
     */
    public void markAsClicked() {
        this.wasClicked = true;
        this.clickedAt = LocalDateTime.now();

        if (this.shownAt != null) {
            this.timeToClickMs = java.time.Duration.between(shownAt, clickedAt).toMillis();
        }

        this.status = RecommendationStatus.CLICKED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark as converted
     */
    public void markAsConverted(String conversionType, Double conversionValue) {
        this.conversionType = conversionType;
        this.conversionValue = conversionValue;
        this.status = RecommendationStatus.CONVERTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark as dismissed
     */
    public void markAsDismissed() {
        this.status = RecommendationStatus.DISMISSED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark as expired
     */
    public void markAsExpired() {
        this.hasExpired = true;
        this.status = RecommendationStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Approve recommendation
     */
    public void approve() {
        this.status = RecommendationStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reject recommendation
     */
    public void reject() {
        this.status = RecommendationStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update scores
     */
    public void updateScores(Double relevanceScore, Double confidenceScore) {
        this.relevanceScore = relevanceScore;
        this.confidenceScore = confidenceScore;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update rank
     */
    public void updateRank(Integer newRank) {
        this.rank = newRank;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set expiration
     */
    public void setExpiration(LocalDateTime expiresAt) {
        this.validUntil = expiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate click-through rate
     */
    public Double getCTR() {
        if (!wasShown) {
            return 0.0;
        }
        return wasClicked ? 100.0 : 0.0;
    }

    /**
     * Calculate conversion rate
     */
    public Double getConversionRate() {
        if (!wasClicked) {
            return 0.0;
        }
        return (status == RecommendationStatus.CONVERTED) ? 100.0 : 0.0;
    }
}
