package com.gogidix.rapidassist.ai.personalization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a user preference.
 * Stores individual user preferences for personalization.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPreference {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String userId;
    private UserProfile profileId;

    // Preference key and value
    private String preferenceKey;
    private String preferenceValue;
    private String preferenceType;

    // Category and context
    private String category;
    private String context;

    // Priority and weight
    @Builder.Default
    private Integer priority = 0;

    @Builder.Default
    private Double weight = 1.0;

    // Metadata
    private String source;
    private String confidenceLevel;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer hitCount;
    private LocalDateTime lastAccessedAt;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;

    /**
     * Check if preference is currently valid
     */
    public boolean isValid() {
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
     * Record access to this preference
     */
    public void recordAccess() {
        this.lastAccessedAt = LocalDateTime.now();
        if (this.hitCount == null) {
            this.hitCount = 0;
        }
        this.hitCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update preference value
     */
    public void updateValue(String newValue) {
        this.preferenceValue = newValue;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update weight
     */
    public void updateWeight(Double newWeight) {
        this.weight = newWeight;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increase priority
     */
    public void increasePriority() {
        if (this.priority == null) {
            this.priority = 0;
        }
        this.priority++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Decrease priority
     */
    public void decreasePriority() {
        if (this.priority == null || this.priority <= 0) {
            this.priority = 0;
        } else {
            this.priority--;
        }
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
     * Clear expiration
     */
    public void clearExpiration() {
        this.validUntil = null;
        this.updatedAt = LocalDateTime.now();
    }
}
