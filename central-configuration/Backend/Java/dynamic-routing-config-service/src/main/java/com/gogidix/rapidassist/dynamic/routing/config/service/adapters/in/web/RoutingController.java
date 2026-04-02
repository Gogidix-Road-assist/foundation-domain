package com.gogidix.rapidassist.dynamic.routing.config.service.adapters.in.web;

import com.gogidix.rapidassist.dynamic.routing.config.service.application.RoutingService;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.in.RoutingCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Dynamic Routing Configuration Service
 */
@RestController
@RequestMapping("/api/routing")
@CrossOrigin(origins = "*")
public class RoutingController {

    private final RoutingCommand commandService;
    private final RoutingService queryService;

    public RoutingController(RoutingService routingService) {
        this.commandService = routingService;
        this.queryService = routingService;
    }

    // Health

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "dynamic-routing-config-service",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    // Query endpoints

    @GetMapping("/rules")
    public ResponseEntity<List<RoutingRule>> getRules(
            @RequestParam String tenantId,
            @RequestParam(required = false) String environment) {
        if (environment != null) {
            return ResponseEntity.ok(queryService.getRulesByEnvironment(tenantId, environment).join());
        }
        return ResponseEntity.ok(queryService.getRulesByTenant(tenantId).join());
    }

    @GetMapping("/rules/active")
    public ResponseEntity<List<RoutingRule>> getActiveRules(
            @RequestParam String tenantId,
            @RequestParam(defaultValue = "production") String environment) {
        return ResponseEntity.ok(queryService.getActiveRules(tenantId, environment).join());
    }

    @GetMapping("/rules/{ruleId}")
    public ResponseEntity<RoutingRule> getRule(@PathVariable String ruleId) {
        return queryService.getRuleById(ruleId).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rules/match")
    public ResponseEntity<List<RoutingRule>> findMatchingRules(@RequestBody MatchRequest request) {
        List<RoutingRule> rules = queryService.findMatchingRules(
            request.tenantId(),
            request.path(),
            request.headers(),
            request.queryParams()
        ).join();
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/rules/match/highest-priority")
    public ResponseEntity<RoutingRule> findHighestPriorityMatch(@RequestBody MatchRequest request) {
        return queryService.findHighestPriorityMatch(
            request.tenantId(),
            request.path(),
            request.headers(),
            request.queryParams()
        ).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Command endpoints

    @PostMapping("/rules")
    public ResponseEntity<RoutingRule> createRule(@Valid @RequestBody CreateRuleRequest request) {
        RoutingCommand.CreateRuleCommand command = new RoutingCommand.CreateRuleCommand(
            request.tenantId(),
            request.ruleName(),
            request.pattern(),
            request.target(),
            request.strategy(),
            request.conditions(),
            request.config(),
            request.priority(),
            request.environment(),
            request.createdBy()
        );

        RoutingRule rule = commandService.createRule(command).join();
        return ResponseEntity.created(URI.create("/api/routing/rules/" + rule.id())).body(rule);
    }

    @PutMapping("/rules/{ruleId}")
    public ResponseEntity<RoutingRule> updateRule(
            @PathVariable String ruleId,
            @RequestBody UpdateRuleRequest request) {
        RoutingCommand.UpdateRuleCommand command = new RoutingCommand.UpdateRuleCommand(
            request.ruleName(),
            request.pattern(),
            request.target(),
            request.strategy(),
            request.conditions(),
            request.config(),
            request.priority(),
            request.updatedBy()
        );

        return commandService.updateRule(ruleId, command).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rules/{ruleId}/activate")
    public ResponseEntity<RoutingRule> activateRule(
            @PathVariable String ruleId,
            @RequestBody Map<String, String> request) {
        return commandService.activateRule(ruleId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rules/{ruleId}/deactivate")
    public ResponseEntity<RoutingRule> deactivateRule(
            @PathVariable String ruleId,
            @RequestBody Map<String, String> request) {
        return commandService.deactivateRule(ruleId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/rules/{ruleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable String ruleId) {
        boolean deleted = commandService.deleteRule(ruleId).join();
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/rules/bulk-update-priority")
    public ResponseEntity<List<RoutingRule>> bulkUpdatePriority(@RequestBody BulkPriorityRequest request) {
        RoutingCommand.BulkPriorityCommand command = new RoutingCommand.BulkPriorityCommand(
            request.ruleIds(),
            request.priorities(),
            request.updatedBy()
        );
        List<RoutingRule> results = commandService.bulkUpdatePriority(command).join();
        return ResponseEntity.ok(results);
    }

    @PostMapping("/rules/bulk-activate")
    public ResponseEntity<List<RoutingRule>> bulkActivate(
            @RequestBody BulkOperationRequest request) {
        List<RoutingRule> results = commandService.bulkActivate(request.ruleIds(), request.updatedBy()).join();
        return ResponseEntity.ok(results);
    }

    @PostMapping("/rules/bulk-deactivate")
    public ResponseEntity<List<RoutingRule>> bulkDeactivate(
            @RequestBody BulkOperationRequest request) {
        List<RoutingRule> results = commandService.bulkDeactivate(request.ruleIds(), request.updatedBy()).join();
        return ResponseEntity.ok(results);
    }

    // Request DTOs

    record MatchRequest(
        String tenantId,
        String path,
        Map<String, String> headers,
        Map<String, String> queryParams
    ) {}

    record CreateRuleRequest(
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

    record UpdateRuleRequest(
        String ruleName,
        RoutingRule.RoutePattern pattern,
        RoutingRule.RouteTarget target,
        RoutingRule.RoutingStrategy strategy,
        List<RoutingRule.Condition> conditions,
        RoutingRule.RouteConfig config,
        int priority,
        String updatedBy
    ) {}

    record BulkPriorityRequest(
        List<String> ruleIds,
        List<Integer> priorities,
        String updatedBy
    ) {}

    record BulkOperationRequest(
        List<String> ruleIds,
        String updatedBy
    ) {}
}
