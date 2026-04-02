package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;
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
 * MongoDB document for ModerationRule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "moderation_rules")
public class ModerationRuleDocument {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private ModerationRule.RuleType ruleType;

    private ModerationRule.RuleSeverity severity;

    private List<String> keywords;

    private List<String> patterns;

    private Map<String, Object> metadata;

    @Indexed
    private boolean active;

    @Indexed
    private Integer priority;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;
}
