package com.gogidix.rapidassist.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an analytics cache entry.
 * Caches computed analytics results for faster retrieval.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsCache {

    private UUID id;
    private String tenantId;
    private String cacheKey;
    private String cacheType;
    private Object cachedData;
    private Map<String, Object> queryParameters;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastAccessedAt;
    private Integer accessCount;
    private Long sizeInBytes;
    private String status;
    private String createdBy;
    private Integer ttlSeconds;
    private Map<String, Object> metadata;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Check if cache is expired
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Business logic: Check if cache is valid
     */
    public boolean isValid() {
        return "ACTIVE".equals(status) && !isExpired();
    }

    /**
     * Business logic: Increment access count
     */
    public void incrementAccessCount() {
        this.accessCount = (this.accessCount == null ? 0 : this.accessCount) + 1;
        this.lastAccessedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get cache age in seconds
     */
    public long getAgeInSeconds() {
        return createdAt != null ? java.time.Duration.between(createdAt, LocalDateTime.now()).getSeconds() : 0;
    }

    /**
     * Business logic: Get time to expiry in seconds
     */
    public long getTimeToExpirySeconds() {
        if (expiresAt == null) {
            return -1;
        }
        long seconds = java.time.Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        return seconds > 0 ? seconds : 0;
    }

    /**
     * Business logic: Refresh cache expiry
     */
    public void refreshExpiry(int additionalTtlSeconds) {
        this.expiresAt = LocalDateTime.now().plusSeconds(additionalTtlSeconds);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as invalid
     */
    public void markAsInvalid() {
        this.status = "INVALID";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if cache is frequently accessed
     */
    public boolean isFrequentlyAccessed(int threshold) {
        return accessCount != null && accessCount >= threshold;
    }

    /**
     * Business logic: Calculate hit rate (would be tracked externally)
     */
    public double getHitRate(int totalRequests) {
        if (totalRequests == 0) {
            return 0.0;
        }
        return (accessCount == null ? 0 : accessCount) * 100.0 / totalRequests;
    }
}
