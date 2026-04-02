package com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.in;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Input port for routing command operations
 */
public interface RoutingCommand {

    CompletableFuture<RoutingRule> createRule(CreateRuleCommand command);
    CompletableFuture<Optional<RoutingRule>> updateRule(String ruleId, UpdateRuleCommand command);
    CompletableFuture<Optional<RoutingRule>> activateRule(String ruleId, String updatedBy);
    CompletableFuture<Optional<RoutingRule>> deactivateRule(String ruleId, String updatedBy);
    CompletableFuture<Boolean> deleteRule(String ruleId);

    CompletableFuture<List<RoutingRule>> bulkUpdatePriority(BulkPriorityCommand command);
    CompletableFuture<List<RoutingRule>> bulkActivate(List<String> ruleIds, String updatedBy);
    CompletableFuture<List<RoutingRule>> bulkDeactivate(List<String> ruleIds, String updatedBy);

    record CreateRuleCommand(
        String tenantId,
        String ruleName,
        RoutingRule.RoutePattern pattern,
        RoutingRule.RouteTarget target,
        RoutingRule.RoutingStrategy strategy,
        List<RoutingRule.Condition> conditions,
        RoutingRule.RouteConfig config,
        int priority,
        String environment,
        String createdBy
    ) {}

    record UpdateRuleCommand(
        String ruleName,
        RoutingRule.RoutePattern pattern,
        RoutingRule.RouteTarget target,
        RoutingRule.RoutingStrategy strategy,
        List<RoutingRule.Condition> conditions,
        RoutingRule.RouteConfig config,
        int priority,
        String updatedBy
    ) {}

    record BulkPriorityCommand(
        List<String> ruleIds,
        List<Integer> priorities,
        String updatedBy
    ) {}
}
