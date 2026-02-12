package com.gogidix.rapidassist.ai.moderation.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Domain model representing the result of content moderation.
 * Contains the outcome of automated moderation analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "moderation_results")
public class ModerationResult {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    private String contentType;

    private String content;

    @Indexed
    private ModerationStatus status;

    @Builder.Default
    private Double confidenceScore = 0.0;

    private List<RuleViolation> violations;

    private Map<String, Object> analysisDetails;

    private String moderatedBy;

    private String reviewedBy;

    @Indexed
    private LocalDateTime moderatedAt;

    private LocalDateTime createdAt;

    private String reviewNotes;

    private Map<String, Object> metadata;

    public enum ModerationStatus {
        APPROVED,
        REJECTED,
        FLAGGED,
        PENDING_REVIEW,
        AUTO_APPROVED,
        AUTO_REJECTED
    }

    /**
     * Check if content is approved
     */
    public boolean isApproved() {
        return status == ModerationStatus.APPROVED || status == ModerationStatus.AUTO_APPROVED;
    }

    /**
     * Check if content is rejected
     */
    public boolean isRejected() {
        return status == ModerationStatus.REJECTED || status == ModerationStatus.AUTO_REJECTED;
    }

    /**
     * Check if content requires manual review
     */
    public boolean requiresReview() {
        return status == ModerationStatus.FLAGGED || status == ModerationStatus.PENDING_REVIEW;
    }

    /**
     * Get severity level of most critical violation
     */
    public ModerationRule.RuleSeverity getHighestSeverity() {
        if (violations == null || violations.isEmpty()) {
            return null;
        }
        return violations.stream()
            .map(RuleViolation::getSeverity)
            .max((s1, s2) -> s1.ordinal() - s2.ordinal())
            .orElse(ModerationRule.RuleSeverity.LOW);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleViolation {
        private String ruleId;
        private String ruleName;
        private ModerationRule.RuleType ruleType;
        private ModerationRule.RuleSeverity severity;
        private String description;
        private String matchedText;
        private Integer position;
    }
}
