package com.gogidix.rapidassist.anti.fraud.rules.service.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request DTO for creating an anti-fraud rule.
 */
@Schema(description = "Request to create a new anti-fraud rule")
@JsonInclude(Include.NON_NULL)
public record CreateRuleRequest(

        @Schema(
                description = "Name of the rule",
                example = "High Value Transaction Alert",
                required = true
        )
        @NotBlank(message = "name cannot be blank")
        String name,

        @Schema(
                description = "Detailed description of the rule",
                example = "Alert on transactions above $10,000"
        )
        String description,

        @Schema(
                description = "Type of fraud detection rule",
                example = "THRESHOLD",
                required = true
        )
        @NotNull(message = "ruleType cannot be null")
        AntiFraudRule.RuleType ruleType,

        @Schema(
                description = "Whether the rule is active",
                example = "true"
        )
        boolean active,

        @Schema(
                description = "Priority for rule evaluation (higher = evaluated first)",
                example = "100"
        )
        int priority,

        @Schema(
                description = "Conditions that trigger the rule",
                example = "{\"amount\": {\"$gt\": 10000}}"
        )
        Map<String, Object> conditions,

        @Schema(
                description = "Actions to take when rule is triggered",
                example = "{\"action\": \"alert\", \"recipients\": [\"fraud@example.com\"]}"
        )
        Map<String, Object> actions
) {
    public CreateRuleRequest {
        if (priority < 0) {
            throw new IllegalArgumentException("priority cannot be negative");
        }
    }
}
