package com.gogidix.rapidassist.feature.flags.service.adapters.in.web;

import com.gogidix.rapidassist.feature.flags.service.application.FeatureFlagService;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlagChange;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlagEvaluation;
import com.gogidix.rapidassist.feature.flags.service.domain.port.in.FeatureFlagCommand;
import com.gogidix.rapidassist.feature.flags.service.domain.port.in.FeatureFlagQuery;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST Controller for Feature Flag Service
 */
@RestController
@RequestMapping("/api/feature-flags")
@CrossOrigin(origins = "*")
public class FeatureFlagController {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagController.class);

    private final FeatureFlagQuery queryService;
    private final FeatureFlagCommand commandService;

    public FeatureFlagController(FeatureFlagService featureFlagService) {
        this.queryService = featureFlagService;
        this.commandService = featureFlagService;
    }

    // Health & Status endpoints

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "feature-flags-service",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    // Evaluation endpoints

    @PostMapping("/evaluate/{flagKey}")
    public ResponseEntity<FeatureFlagEvaluation> evaluateFlag(
            @PathVariable String flagKey,
            @RequestBody EvaluationRequest request) {
        FeatureFlagQuery.EvaluationContext context = new FeatureFlagQuery.EvaluationContext(
            request.userId(),
            request.tenantId(),
            request.countryCode(),
            request.environment(),
            request.attributes()
        );
        FeatureFlagEvaluation result = queryService.evaluateFlag(flagKey, request.tenantId(), context).join();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/enabled/{flagKey}")
    public ResponseEntity<Map<String, Boolean>> isFlagEnabled(
            @PathVariable String flagKey,
            @RequestParam String tenantId,
            @RequestParam String userId) {
        Boolean enabled = queryService.isFlagEnabled(flagKey, tenantId, userId).join();
        return ResponseEntity.ok(Map.of("enabled", enabled));
    }

    @PostMapping("/evaluate-all")
    public ResponseEntity<List<FeatureFlagEvaluation>> evaluateAllFlags(
            @RequestBody EvaluationRequest request) {
        FeatureFlagQuery.EvaluationContext context = new FeatureFlagQuery.EvaluationContext(
            request.userId(),
            request.tenantId(),
            request.countryCode(),
            request.environment(),
            request.attributes()
        );
        List<FeatureFlagEvaluation> results = queryService.evaluateAllFlags(request.tenantId(), context).join();
        return ResponseEntity.ok(results);
    }

    // Query endpoints

    @GetMapping
    public ResponseEntity<List<FeatureFlag>> getAllFlags(
            @RequestParam String tenantId,
            @RequestParam(required = false) String environment) {
        if (environment != null) {
            return ResponseEntity.ok(queryService.getFlagsByEnvironment(tenantId, environment).join());
        }
        return ResponseEntity.ok(queryService.getAllFlags(tenantId).join());
    }

    @GetMapping("/{flagId}")
    public ResponseEntity<FeatureFlag> getFlag(@PathVariable String flagId) {
        return queryService.getFlagById(flagId).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<FeatureFlag> getFlagByKey(
            @RequestParam String tenantId,
            @PathVariable String key,
            @RequestParam(defaultValue = "production") String environment) {
        return queryService.getFlagByKey(tenantId, key, environment).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<FeatureFlag>> getActiveFlags(
            @RequestParam String tenantId,
            @RequestParam(defaultValue = "production") String environment) {
        return ResponseEntity.ok(queryService.getActiveFlags(tenantId, environment).join());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeatureFlag>> getFlagsByStatus(
            @RequestParam String tenantId,
            @PathVariable FeatureFlag.FlagStatus status) {
        return ResponseEntity.ok(queryService.getFlagsByStatus(tenantId, status).join());
    }

    @GetMapping("/tags")
    public ResponseEntity<List<FeatureFlag>> getFlagsByTags(
            @RequestParam String tenantId,
            @RequestParam Set<String> tags) {
        return ResponseEntity.ok(queryService.getFlagsByTags(tenantId, tags).join());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FeatureFlag>> searchFlags(
            @RequestParam String tenantId,
            @RequestParam String keyword) {
        return ResponseEntity.ok(queryService.searchFlags(tenantId, keyword).join());
    }

    @GetMapping("/statistics")
    public ResponseEntity<FeatureFlagQuery.FlagStatistics> getStatistics(
            @RequestParam String tenantId) {
        return ResponseEntity.ok(queryService.getStatistics(tenantId).join());
    }

    @GetMapping("/{flagId}/history")
    public ResponseEntity<List<FeatureFlagChange>> getFlagHistory(@PathVariable String flagId) {
        return ResponseEntity.ok(queryService.getFlagHistory(flagId).join());
    }

    @GetMapping("/changes/pending")
    public ResponseEntity<List<FeatureFlagChange>> getPendingApprovals(
            @RequestParam String tenantId) {
        return ResponseEntity.ok(queryService.getPendingApprovals(tenantId).join());
    }

    // Command endpoints

    @PostMapping
    public ResponseEntity<FeatureFlag> createFlag(@Valid @RequestBody CreateFlagRequest request) {
        FeatureFlagCommand.CreateFlagCommand command = new FeatureFlagCommand.CreateFlagCommand(
            request.tenantId(),
            request.key(),
            request.name(),
            request.description(),
            request.type(),
            request.rolloutStrategy(),
            request.environment(),
            request.createdBy(),
            request.tags(),
            request.requiresApproval(),
            request.expiresAt(),
            request.allowedTenants(),
            request.allowedUsers(),
            request.allowedCountries(),
            request.percentageRollout()
        );

        FeatureFlag flag = commandService.createFlag(command).join();
        return ResponseEntity.created(URI.create("/api/feature-flags/" + flag.id())).body(flag);
    }

    @PutMapping("/{flagId}")
    public ResponseEntity<FeatureFlag> updateFlag(
            @PathVariable String flagId,
            @RequestBody UpdateFlagRequest request) {
        FeatureFlagCommand.UpdateFlagCommand command = new FeatureFlagCommand.UpdateFlagCommand(
            flagId,
            request.name(),
            request.description(),
            request.type(),
            request.rolloutStrategy(),
            request.updatedBy(),
            request.reason()
        );

        return commandService.updateFlag(command).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{flagId}/enable")
    public ResponseEntity<FeatureFlag> enableFlag(
            @PathVariable String flagId,
            @RequestBody EnableDisableRequest request) {
        return commandService.enableFlag(flagId, request.changedBy(), request.reason()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{flagId}/disable")
    public ResponseEntity<FeatureFlag> disableFlag(
            @PathVariable String flagId,
            @RequestBody EnableDisableRequest request) {
        return commandService.disableFlag(flagId, request.changedBy(), request.reason()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{flagId}/archive")
    public ResponseEntity<FeatureFlag> archiveFlag(
            @PathVariable String flagId,
            @RequestBody Map<String, String> request) {
        return commandService.archiveFlag(flagId, request.get("changedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{flagId}/rollout-percentage")
    public ResponseEntity<FeatureFlag> updateRolloutPercentage(
            @PathVariable String flagId,
            @RequestBody RolloutPercentageRequest request) {
        return commandService.updateRolloutPercentage(flagId, request.percentage(), request.changedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{flagId}/users/add")
    public ResponseEntity<FeatureFlag> addAllowedUsers(
            @PathVariable String flagId,
            @RequestBody UsersRequest request) {
        return commandService.addAllowedUsers(flagId, request.userIds(), request.changedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{flagId}/users/remove")
    public ResponseEntity<FeatureFlag> removeAllowedUsers(
            @PathVariable String flagId,
            @RequestBody UsersRequest request) {
        return commandService.removeAllowedUsers(flagId, request.userIds(), request.changedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{flagId}/tenants/add")
    public ResponseEntity<FeatureFlag> addAllowedTenants(
            @PathVariable String flagId,
            @RequestBody TenantsRequest request) {
        return commandService.addAllowedTenants(flagId, request.tenantIds(), request.changedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{flagId}/countries/add")
    public ResponseEntity<FeatureFlag> addAllowedCountries(
            @PathVariable String flagId,
            @RequestBody CountriesRequest request) {
        return commandService.addAllowedCountries(flagId, request.countryCodes(), request.changedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bulk-enable")
    public ResponseEntity<List<FeatureFlag>> bulkEnable(
            @RequestBody BulkOperationRequest request) {
        List<FeatureFlag> results = commandService.bulkEnable(request.flagIds(), request.changedBy()).join();
        return ResponseEntity.ok(results);
    }

    @PostMapping("/bulk-disable")
    public ResponseEntity<List<FeatureFlag>> bulkDisable(
            @RequestBody BulkOperationRequest request) {
        List<FeatureFlag> results = commandService.bulkDisable(request.flagIds(), request.changedBy()).join();
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{flagId}")
    public ResponseEntity<Void> deleteFlag(
            @PathVariable String flagId,
            @RequestParam String deletedBy) {
        boolean deleted = commandService.deleteFlag(flagId, deletedBy).join();
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/changes/{changeId}/approve")
    public ResponseEntity<FeatureFlagChange> approveChange(
            @PathVariable String changeId,
            @RequestBody Map<String, String> request) {
        FeatureFlagChange change = commandService.approveChange(changeId, request.get("approvedBy")).join();
        return ResponseEntity.ok(change);
    }

    @PostMapping("/changes/{changeId}/reject")
    public ResponseEntity<FeatureFlagChange> rejectChange(
            @PathVariable String changeId,
            @RequestBody RejectRequest request) {
        FeatureFlagChange change = commandService.rejectChange(changeId, request.rejectedBy(), request.reason()).join();
        return ResponseEntity.ok(change);
    }

    // Request DTOs

    record EvaluationRequest(
        String tenantId,
        String userId,
        String countryCode,
        String environment,
        Map<String, Object> attributes
    ) {}

    record CreateFlagRequest(
        String tenantId,
        String key,
        String name,
        String description,
        FeatureFlag.FlagType type,
        FeatureFlag.RolloutStrategy rolloutStrategy,
        String environment,
        String createdBy,
        Set<String> tags,
        Boolean requiresApproval,
        Instant expiresAt,
        Set<String> allowedTenants,
        Set<String> allowedUsers,
        Set<String> allowedCountries,
        FeatureFlag.PercentageRollout percentageRollout
    ) {}

    record UpdateFlagRequest(
        String name,
        String description,
        FeatureFlag.FlagType type,
        FeatureFlag.RolloutStrategy rolloutStrategy,
        String updatedBy,
        String reason
    ) {}

    record EnableDisableRequest(
        String changedBy,
        String reason
    ) {}

    record RolloutPercentageRequest(
        int percentage,
        String changedBy
    ) {}

    record UsersRequest(
        Set<String> userIds,
        String changedBy
    ) {}

    record TenantsRequest(
        Set<String> tenantIds,
        String changedBy
    ) {}

    record CountriesRequest(
        Set<String> countryCodes,
        String changedBy
    ) {}

    record BulkOperationRequest(
        List<String> flagIds,
        String changedBy
    ) {}

    record RejectRequest(
        String rejectedBy,
        String reason
    ) {}
}
