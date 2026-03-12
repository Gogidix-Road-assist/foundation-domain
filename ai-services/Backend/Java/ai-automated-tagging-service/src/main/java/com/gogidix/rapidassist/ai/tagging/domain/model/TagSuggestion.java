package com.gogidix.rapidassist.ai.tagging.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a TagSuggestion.
 * AI-powered tag suggestions for content.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagSuggestion {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String contentId;
    private String contentType;
    private UUID suggestedTagId;
    private Double confidenceScore;
    private SuggestionStatus status;
    private String suggestionMetadata;
    private String aiModelUsed;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String createdBy;
    private Long version;

    /**
     * Enum representing suggestion status
     */
    public enum SuggestionStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        EXPIRED
    }

    /**
     * Checks if the suggestion is still pending
     */
    public boolean isPending() {
        return this.status == SuggestionStatus.PENDING;
    }

    /**
     * Marks the suggestion as accepted
     */
    public void accept() {
        this.status = SuggestionStatus.ACCEPTED;
    }

    /**
     * Marks the suggestion as rejected
     */
    public void reject() {
        this.status = SuggestionStatus.REJECTED;
    }

    /**
     * Marks the suggestion as expired
     */
    public void markAsExpired() {
        this.status = SuggestionStatus.EXPIRED;
    }

    /**
     * Checks if the suggestion is expired
     */
    public boolean isExpired() {
        return this.status == SuggestionStatus.EXPIRED ||
               (this.expiresAt != null && java.time.LocalDateTime.now().isAfter(this.expiresAt));
    }

    /**
     * Checks if the suggestion has high confidence (>= 0.8)
     */
    public boolean isHighConfidence() {
        return this.confidenceScore != null && this.confidenceScore >= 0.8;
    }

    /**
     * Checks if the suggestion has medium confidence (>= 0.5 and < 0.8)
     */
    public boolean isMediumConfidence() {
        return this.confidenceScore != null &&
               this.confidenceScore >= 0.5 &&
               this.confidenceScore < 0.8;
    }
}
