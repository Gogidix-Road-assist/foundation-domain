package com.gogidix.rapidassist.anti.fraud.rules.service.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Response DTO for an anti-fraud rule.
 */
@Schema(description = "Response containing anti-fraud rule details")
@JsonInclude(Include.NON_NULL)
public record RuleResponse(

        @Schema(description = "Unique identifier of the rule", example = "507f1f77bcf86cd799439011")
        String id,

        @Schema(description = "Tenant identifier", example = "tenant-123")
        String tenantId,

        @Schema(description = "Name of the rule", example = "High Value Transaction Alert")
        String name,

        @Schema(description = "Detailed description of the rule", example = "Alert on transactions above $10,000")
        String description,

        @Schema(description = "Type of fraud detection rule", example = "THRESHOLD")
        AntiFraudRule.RuleType ruleType,

        @Schema(description = "Whether the rule is active", example = "true")
        boolean active,

        @Schema(description = "Priority for rule evaluation", example = "100")
        int priority,

        @Schema(description = "Conditions that trigger the rule")
        Map<String, Object> conditions,

        @Schema(description = "Actions to take when rule is triggered")
        Map<String, Object> actions,

        @Schema(description = "Timestamp when the rule was created", example = "2024-01-01T00:00:00Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the rule was last updated", example = "2024-01-01T00:00:00Z")
        Instant updatedAt,

        @Schema(description = "User who created the rule", example = "admin@example.com")
        String createdBy,

        @Schema(description = "User who last updated the rule", example = "admin@example.com")
        String updatedBy
) {
    public static RuleResponse fromDomain(AntiFraudRule rule) {
        return new RuleResponse(
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
}
