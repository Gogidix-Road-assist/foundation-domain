package com.gogidix.rapidassist.ai.tagging.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing the junction between Content and Tags.
 * This entity tracks which tags are applied to which content.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentTag {

    private UUID id;
    private String tenantId;
    private String contentId;
    private String contentType;
    private UUID tagId;
    private TaggingSource taggingSource;
    private Double confidenceScore;
    private Boolean manuallyVerified;
    private String taggedBy;
    private String taggingMetadata;
    private LocalDateTime createdAt;
    private String createdBy;
    private Long version;

    /**
     * Tagging source enumeration
     */
    public enum TaggingSource {
        AUTOMATIC_AI,
        AUTOMATIC_RULE,
        MANUAL,
        HYBRID
    }

    /**
     * Check if tagging is from automatic source
     */
    public boolean isAutomatic() {
        return TaggingSource.AUTOMATIC_AI.equals(this.taggingSource) ||
               TaggingSource.AUTOMATIC_RULE.equals(this.taggingSource);
    }

    /**
     * Check if tagging is from manual source
     */
    public boolean isManual() {
        return TaggingSource.MANUAL.equals(this.taggingSource);
    }

    /**
     * Check if tagging is verified
     */
    public boolean isVerified() {
        return Boolean.TRUE.equals(this.manuallyVerified);
    }

    /**
     * Verify the tagging
     */
    public void verify() {
        this.manuallyVerified = true;
    }

    /**
     * Unverify the tagging
     */
    public void unverify() {
        this.manuallyVerified = false;
    }
}
