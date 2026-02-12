package com.gogidix.rapidassist.ai.personalization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Enumeration representing the status of a user profile.
 */
// Note: UserProfileStatus is in a separate file

/**
 * Domain model representing a user's personalization profile.
 * Contains user behavioral data, demographics, and preferences
 * used for generating personalized recommendations.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    private UUID id;
    private String tenantId;
    private String userId;
    private String segmentId;

    // Demographics
    private Integer age;
    private String gender;
    private String location;
    private String language;
    private String timezone;

    // Behavioral data
    private Integer totalSessions;
    private Integer totalInteractions;
    private LocalDateTime lastActivityAt;
    private LocalDateTime firstSeenAt;

    // Preferences and interests
    private Map<String, Object> interests;
    private Map<String, Object> preferences;

    // Metrics
    private Double engagementScore;
    private Double loyaltyScore;
    private Double satisfactionScore;

    // Status
    @Builder.Default
    private UserProfileStatus status = com.gogidix.rapidassist.ai.personalization.domain.model.UserProfileStatus.ACTIVE;

    // Metadata
    private Map<String, Object> attributes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;

    /**
     * Calculate engagement score based on user activity
     */
    public void calculateEngagementScore() {
        if (totalSessions == null || totalInteractions == null) {
            this.engagementScore = 0.0;
            return;
        }

        double sessionScore = Math.min(totalSessions / 100.0, 1.0);
        double interactionScore = Math.min(totalInteractions / 1000.0, 1.0);
        double recencyScore = calculateRecencyScore();

        this.engagementScore = (sessionScore * 0.3 + interactionScore * 0.4 + recencyScore * 0.3) * 100;
    }

    /**
     * Calculate loyalty score based on user history
     */
    public void calculateLoyaltyScore() {
        if (firstSeenAt == null) {
            this.loyaltyScore = 0.0;
            return;
        }

        long daysSinceFirstSeen = java.time.temporal.ChronoUnit.DAYS.between(
            firstSeenAt, LocalDateTime.now());

        double tenureScore = Math.min(daysSinceFirstSeen / 365.0, 1.0) * 40;
        double engagementScore = (this.engagementScore != null ? this.engagementScore * 0.6 : 0.0);

        this.loyaltyScore = tenureScore + engagementScore;
    }

    private double calculateRecencyScore() {
        if (lastActivityAt == null) {
            return 0.0;
        }

        long daysSinceLastActivity = java.time.temporal.ChronoUnit.DAYS.between(
            lastActivityAt, LocalDateTime.now());

        return Math.max(0.0, 1.0 - (daysSinceLastActivity / 30.0));
    }

    /**
     * Check if profile is active
     */
    public boolean isActive() {
        return status == com.gogidix.rapidassist.ai.personalization.domain.model.UserProfileStatus.ACTIVE;
    }

    /**
     * Mark as inactive
     */
    public void deactivate() {
        this.status = com.gogidix.rapidassist.ai.personalization.domain.model.UserProfileStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reactivate profile
     */
    public void activate() {
        this.status = com.gogidix.rapidassist.ai.personalization.domain.model.UserProfileStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update activity tracking
     */
    public void recordActivity() {
        if (this.totalSessions == null) {
            this.totalSessions = 0;
        }
        if (this.totalInteractions == null) {
            this.totalInteractions = 0;
        }

        this.totalSessions++;
        this.lastActivityAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.firstSeenAt == null) {
            this.firstSeenAt = LocalDateTime.now();
        }

        calculateEngagementScore();
        calculateLoyaltyScore();
    }

    /**
     * Add interaction count
     */
    public void addInteraction(int count) {
        if (this.totalInteractions == null) {
            this.totalInteractions = 0;
        }
        this.totalInteractions += count;
        this.lastActivityAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateEngagementScore();
        calculateLoyaltyScore();
    }

    /**
     * Get preference value
     */
    public Object getPreference(String key) {
        return preferences != null ? preferences.get(key) : null;
    }

    /**
     * Set preference value
     */
    public void setPreference(String key, Object value) {
        if (this.preferences == null) {
            this.preferences = new java.util.HashMap<>();
        }
        this.preferences.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get attribute value
     */
    public Object getAttribute(String key) {
        return attributes != null ? attributes.get(key) : null;
    }

    /**
     * Set attribute value
     */
    public void setAttribute(String key, Object value) {
        if (this.attributes == null) {
            this.attributes = new java.util.HashMap<>();
        }
        this.attributes.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
}
