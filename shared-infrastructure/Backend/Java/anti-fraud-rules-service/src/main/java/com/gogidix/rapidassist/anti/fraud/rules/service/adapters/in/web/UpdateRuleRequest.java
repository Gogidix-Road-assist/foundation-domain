package com.gogidix.rapidassist.anti.fraud.rules.service.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request DTO for updating an anti-fraud rule.
 * All fields are optional - only provided fields will be updated.
 */
@Schema(description = "Request to update an existing anti-fraud rule")
@JsonInclude(Include.NON_NULL)
public record UpdateRuleRequest(

        @Schema(
                description = "Updated name of the rule",
                example = "Updated High Value Transaction Alert"
        )
        String name,

        @Schema(
                description = "Updated description of the rule",
                example = "Alert on transactions above $15,000"
        )
        String description,

        @Schema(
                description = "Updated type of fraud detection rule",
                example = "THRESHOLD"
        )
        AntiFraudRule.RuleType ruleType,

        @Schema(
                description = "Whether the rule is active",
                example = "false"
        )
        Boolean active,

        @Schema(
                description = "Updated priority for rule evaluation",
                example = "150"
        )
        @Min(message = "priority cannot be negative", value = 0)
        Integer priority,

        @Schema(
                description = "Updated conditions that trigger the rule",
                example = "{\"amount\": {\"$gt\": 15000}}"
        )
        Map<String, Object> conditions,

        @Schema(
                description = "Updated actions to take when rule is triggered",
                example = "{\"action\": \"block\", \"recipients\": [\"fraud@example.com\"]}"
        )
        Map<String, Object> actions
) {}
