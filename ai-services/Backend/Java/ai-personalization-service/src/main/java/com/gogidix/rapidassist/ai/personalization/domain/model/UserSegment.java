package com.gogidix.rapidassist.ai.personalization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a user segment.
 * Groups users with similar characteristics for targeted personalization.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSegment {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String segmentName;
    private String segmentCode;
    private String description;

    // Segment definition
    private String segmentType;
    private Map<String, Object> criteria;
    private List<String> includedUserIds;
    private List<String> excludedUserIds;

    // Size and scope
    private Integer size;
    private Integer maxCapacity;

    // Priority and display
    @Builder.Default
    private Integer priority = 0;

    private String color;
    private String icon;
    private Boolean isPublic;

    // Status
    @Builder.Default
    private SegmentStatus status = SegmentStatus.ACTIVE;

    // Auto-update settings
    private Boolean autoUpdate;
    private String updateFrequency;
    private LocalDateTime lastCalculatedAt;
    private LocalDateTime nextCalculationAt;

    // Metadata
    private String category;
    private List<String> tags;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;

    /**
     * Check if segment is active
     */
    public boolean isActive() {
        return status == SegmentStatus.ACTIVE;
    }

    /**
     * Activate segment
     */
    public void activate() {
        this.status = SegmentStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivate segment
     */
    public void deactivate() {
        this.status = SegmentStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Archive segment
     */
    public void archive() {
        this.status = SegmentStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if user is in segment
     */
    public boolean containsUser(String userId) {
        if (excludedUserIds != null && excludedUserIds.contains(userId)) {
            return false;
        }

        if (includedUserIds == null || includedUserIds.isEmpty()) {
            return false;
        }

        return includedUserIds.contains(userId);
    }

    /**
     * Add user to segment
     */
    public void addUser(String userId) {
        if (this.includedUserIds == null) {
            this.includedUserIds = new java.util.ArrayList<>();
        }

        if (!this.includedUserIds.contains(userId)) {
            this.includedUserIds.add(userId);
            if (this.size == null) {
                this.size = 0;
            }
            this.size++;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove user from segment
     */
    public void removeUser(String userId) {
        if (this.includedUserIds != null && this.includedUserIds.remove(userId)) {
            if (this.size != null && this.size > 0) {
                this.size--;
            }
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Exclude user from segment
     */
    public void excludeUser(String userId) {
        if (this.excludedUserIds == null) {
            this.excludedUserIds = new java.util.ArrayList<>();
        }

        if (!this.excludedUserIds.contains(userId)) {
            this.excludedUserIds.add(userId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove user from exclusion list
     */
    public void removeExclusion(String userId) {
        if (this.excludedUserIds != null && this.excludedUserIds.remove(userId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Calculate segment size
     */
    public void calculateSize() {
        this.size = (includedUserIds != null) ? includedUserIds.size() : 0;
        this.lastCalculatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set auto-update
     */
    public void setAutoUpdate(Boolean enabled, String frequency) {
        this.autoUpdate = enabled;
        this.updateFrequency = frequency;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Add tag
     */
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new java.util.ArrayList<>();
        }
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove tag
     */
    public void removeTag(String tag) {
        if (this.tags != null) {
            this.tags.remove(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Check if segment is at capacity
     */
    public boolean isAtCapacity() {
        return maxCapacity != null && size != null && size >= maxCapacity;
    }
}
