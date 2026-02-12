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
 * Domain model representing an action taken on moderated content.
 * Tracks all moderation actions for audit purposes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "moderation_actions")
public class ModerationAction {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    private String moderationResultId;

    @Indexed
    private ActionType actionType;

    private String previousStatus;

    private String newStatus;

    private String performedBy;

    private String performedByName;

    @Indexed
    private LocalDateTime performedAt;

    private String reason;

    private String notes;

    private Map<String, Object> actionDetails;

    private String sourceIp;

    private String userAgent;

    private Map<String, Object> metadata;

    public enum ActionType {
        APPROVE,
        REJECT,
        FLAG,
        UNFLAG,
        ESCALATE,
        REQUEST_CHANGES,
        AUTO_APPROVE,
        AUTO_REJECT,
        AUTO_FLAG,
        MANUAL_REVIEW
    }

    /**
     * Create an approve action
     */
    public static ModerationAction createApproveAction(String contentId, String performedBy, String reason) {
        return ModerationAction.builder()
            .contentId(contentId)
            .actionType(ActionType.APPROVE)
            .performedBy(performedBy)
            .performedAt(LocalDateTime.now())
            .reason(reason)
            .build();
    }

    /**
     * Create a reject action
     */
    public static ModerationAction createRejectAction(String contentId, String performedBy, String reason) {
        return ModerationAction.builder()
            .contentId(contentId)
            .actionType(ActionType.REJECT)
            .performedBy(performedBy)
            .performedAt(LocalDateTime.now())
            .reason(reason)
            .build();
    }

    /**
     * Create a flag action
     */
    public static ModerationAction createFlagAction(String contentId, String performedBy, String reason) {
        return ModerationAction.builder()
            .contentId(contentId)
            .actionType(ActionType.FLAG)
            .performedBy(performedBy)
            .performedAt(LocalDateTime.now())
            .reason(reason)
            .build();
    }

    /**
     * Check if this is an automated action
     */
    public boolean isAutomated() {
        return actionType == ActionType.AUTO_APPROVE ||
               actionType == ActionType.AUTO_REJECT ||
               actionType == ActionType.AUTO_FLAG;
    }

    /**
     * Check if this is a manual action
     */
    public boolean isManual() {
        return !isAutomated();
    }
}
