package com.gogidix.rapidassist.anti.fraud.rules.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * MongoDB document for AntiFraudRule.
 * CRITICAL: Compound index on (tenantId, active) for efficient tenant-isolated queries.
 */
@Document(collection = "anti_fraud_rules")
@CompoundIndex(name = "idx_tenant_active", def = "{'tenantId': 1, 'active': 1}")
@CompoundIndex(name = "idx_tenant_priority", def = "{'tenantId': 1, 'priority': -1}")
@CompoundIndex(name = "idx_tenant_type_active", def = "{'tenantId': 1, 'ruleType': 1, 'active': 1}")
public record AntiFraudRuleDocument(

        @Id
        String id,

        @Indexed(name = "idx_tenant_id")
        String tenantId,

        String name,

        String description,

        AntiFraudRule.RuleType ruleType,

        boolean active,

        int priority,

        Map<String, Object> conditions,

        Map<String, Object> actions,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {

    public static AntiFraudRuleDocument fromDomain(AntiFraudRule rule) {
        return new AntiFraudRuleDocument(
                rule.id(),
                rule.tenantId(),
                rule.name(),
                rule.description(),
                rule.ruleType(),
                rule.active(),
                rule.priority(),
                rule.conditions(),
                rule.actions(),
                rule.createdAt(),
                rule.updatedAt(),
                rule.createdBy(),
                rule.updatedBy()
        );
    }

    public AntiFraudRule toDomain() {
        return AntiFraudRule.builder()
                .id(id())
                .tenantId(tenantId())
                .name(name())
                .description(description())
                .ruleType(ruleType())
                .active(active())
                .priority(priority())
                .conditions(conditions())
                .actions(actions())
                .createdAt(createdAt())
                .updatedAt(updatedAt())
                .createdBy(createdBy())
                .updatedBy(updatedBy())
                .build();
    }

    public static AntiFraudRuleDocument updateFromDomain(AntiFraudRuleDocument existing, AntiFraudRule rule) {
        return new AntiFraudRuleDocument(
                existing.id(),
                rule.tenantId(),
                rule.name(),
                rule.description(),
                rule.ruleType(),
                rule.active(),
                rule.priority(),
                rule.conditions(),
                rule.actions(),
                existing.createdAt(),
                Instant.now(),
                existing.createdBy(),
                rule.updatedBy()
        );
    }
}
