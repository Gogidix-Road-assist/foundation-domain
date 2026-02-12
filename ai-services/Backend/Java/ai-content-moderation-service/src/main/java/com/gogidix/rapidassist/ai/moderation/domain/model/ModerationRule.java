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
 * Domain model representing a content moderation rule.
 * Defines what content is allowed or prohibited based on policies.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "moderation_rules")
public class ModerationRule {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private RuleType ruleType;

    private RuleSeverity severity;

    private List<String> keywords;

    private List<String> patterns;

    private Map<String, Object> metadata;

    @Indexed
    private boolean active;

    @Builder.Default
    private Integer priority = 0;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String updatedBy;

    public enum RuleType {
        KEYWORD,
        PATTERN,
        SENTIMENT,
        CATEGORY,
        CUSTOM
    }

    public enum RuleSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Activate this rule
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivate this rule
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if this rule matches the given content
     */
    public boolean matches(String content) {
        if (!active) {
            return false;
        }

        if (ruleType == RuleType.KEYWORD && keywords != null) {
            String lowerContent = content.toLowerCase();
            return keywords.stream()
                .anyMatch(keyword -> lowerContent.contains(keyword.toLowerCase()));
        }

        return false;
    }
}
