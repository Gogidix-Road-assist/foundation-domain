package com.gogidix.rapidassist.ai.moderation.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain model representing a queue item for pending moderation.
 * Used to manage content that requires manual review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "moderation_queue")
public class ModerationQueue {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    private String contentType;

    private String content;

    private String userId;

    private String userName;

    @Indexed
    private QueueStatus status;

    @Indexed
    private Integer priority;

    @Indexed
    private LocalDateTime submittedAt;

    private LocalDateTime assignedAt;

    private String assignedTo;

    private LocalDateTime reviewedAt;

    private String reviewedBy;

    private QueueAction action;

    private String reviewNotes;

    private Map<String, Object> context;

    private Map<String, Object> metadata;

    public enum QueueStatus {
        PENDING,
        ASSIGNED,
        IN_REVIEW,
        COMPLETED,
        CANCELLED
    }

    public enum QueueAction {
        APPROVE,
        REJECT,
        ESCALATE,
        REQUEST_CHANGES
    }

    /**
     * Assign queue item to a reviewer
     */
    public void assignTo(String reviewerId) {
        this.status = QueueStatus.ASSIGNED;
        this.assignedTo = reviewerId;
        this.assignedAt = LocalDateTime.now();
    }

    /**
     * Start review process
     */
    public void startReview() {
        this.status = QueueStatus.IN_REVIEW;
    }

    /**
     * Complete review with action
     */
    public void completeReview(String reviewerId, QueueAction action, String notes) {
        this.status = QueueStatus.COMPLETED;
        this.reviewedBy = reviewerId;
        this.reviewedAt = LocalDateTime.now();
        this.action = action;
        this.reviewNotes = notes;
    }

    /**
     * Cancel queue item
     */
    public void cancel() {
        this.status = QueueStatus.CANCELLED;
    }

    /**
     * Check if item is pending assignment
     */
    public boolean isPending() {
        return status == QueueStatus.PENDING;
    }

    /**
     * Check if item is assigned
     */
    public boolean isAssigned() {
        return status == QueueStatus.ASSIGNED || status == QueueStatus.IN_REVIEW;
    }
}
