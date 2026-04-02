package com.gogidix.rapidassist.ai.moderation.domain.event;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when a moderation rule is created.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationRuleCreatedEvent {

    private String eventId;

    private String tenantId;

    private String ruleId;

    private String ruleName;

    private ModerationRule.RuleType ruleType;

    private LocalDateTime occurredAt;

    public static ModerationRuleCreatedEvent create(String tenantId, String ruleId, String ruleName,
                                                     ModerationRule.RuleType ruleType) {
        return ModerationRuleCreatedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .ruleId(ruleId)
            .ruleName(ruleName)
            .ruleType(ruleType)
            .occurredAt(LocalDateTime.now())
            .build();
    }
}
