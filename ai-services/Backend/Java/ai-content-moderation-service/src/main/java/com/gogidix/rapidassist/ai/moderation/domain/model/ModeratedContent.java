package com.gogidix.rapidassist.ai.moderation.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain model representing content that has been moderated.
 * Stores the original content along with moderation metadata.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "moderated_content")
public class ModeratedContent {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    @Indexed
    private String contentType;

    private String content;

    private String contentHash;

    private String userId;

    private String userName;

    @Indexed
    private ModerationResult.ModerationStatus status;

    private String moderationResultId;

    @Indexed
    private LocalDateTime submittedAt;

    private LocalDateTime moderatedAt;

    private String moderationSource;

    private Map<String, Object> context;

    private Map<String, Object> metadata;

    /**
     * Check if content is pending moderation
     */
    public boolean isPendingModeration() {
        return status == null || status == ModerationResult.ModerationStatus.PENDING_REVIEW;
    }

    /**
     * Mark content as approved
     */
    public void markAsApproved(String moderationResultId) {
        this.status = ModerationResult.ModerationStatus.APPROVED;
        this.moderationResultId = moderationResultId;
        this.moderatedAt = LocalDateTime.now();
    }

    /**
     * Mark content as rejected
     */
    public void markAsRejected(String moderationResultId) {
        this.status = ModerationResult.ModerationStatus.REJECTED;
        this.moderationResultId = moderationResultId;
        this.moderatedAt = LocalDateTime.now();
    }

    /**
     * Mark content as flagged for review
     */
    public void markAsFlagged(String moderationResultId) {
        this.status = ModerationResult.ModerationStatus.FLAGGED;
        this.moderationResultId = moderationResultId;
        this.moderatedAt = LocalDateTime.now();
    }
}
