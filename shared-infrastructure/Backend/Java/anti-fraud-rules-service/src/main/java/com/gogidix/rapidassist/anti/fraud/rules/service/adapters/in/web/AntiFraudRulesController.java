package com.gogidix.rapidassist.anti.fraud.rules.service.adapters.in.web;

import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;
import com.gogidix.rapidassist.anti.fraud.rules.service.domain.port.in.AntiFraudRuleService;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for managing anti-fraud rules with CRITICAL tenant isolation.
 *
 * SECURITY: All endpoints extract tenantId from RequestContext to ensure tenant isolation.
 * - No endpoint accepts tenantId as a request parameter (prevents tenant spoofing)
 * - All operations are scoped to the tenant from the JWT token
 * - Cross-tenant access attempts return 401 Unauthorized
 */
@RestController
@RequestMapping("/api/v1/anti-fraud-rules")
@Tag(name = "Anti-Fraud Rules", description = "APIs for managing anti-fraud detection rules")
@SecurityRequirement(name = "Bearer Authentication")
public class AntiFraudRulesController {

    private static final Logger log = LoggerFactory.getLogger(AntiFraudRulesController.class);

    private final AntiFraudRuleService ruleService;

    public AntiFraudRulesController(AntiFraudRuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * Extract tenantId from RequestContext.
     * CRITICAL: This ensures tenant isolation - tenantId comes from JWT, not from client request.
     */
    private String getTenantId() {
        return RequestContextHolder.get()
                .map(c -> c.tenantId())
                .orElseThrow(() -> {
                    log.error("Missing tenantId in RequestContext");
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                            "Missing tenantId in JWT context");
                });
    }

    /**
     * Get current user from RequestContext (for audit trail).
     */
    private String getCurrentUser() {
        return RequestContextHolder.get()
                .map(c -> c.userId())
                .orElse("system");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new anti-fraud rule",
            description = "Creates a new fraud detection rule for the authenticated tenant. " +
                    "The rule is automatically scoped to the tenant from the JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Rule created successfully",
                    content = @Content(schema = @Schema(implementation = RuleResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters")
    })
    public RuleResponse createRule(
            @Parameter(description = "Rule creation request", required = true)
            @Valid @RequestBody CreateRuleRequest request
    ) {
        String tenantId = getTenantId();
        log.info("Creating rule for tenant: {}, name: {}", tenantId, request.name());

        AntiFraudRuleService.CreateRuleRequest serviceRequest =
                new AntiFraudRuleService.CreateRuleRequest(
                        request.name(),
                        request.description(),
                        request.ruleType(),
                        request.active(),
                        request.priority(),
                        request.conditions(),
                        request.actions(),
                        getCurrentUser()
                );

        AntiFraudRule rule = ruleService.createRule(tenantId, serviceRequest);
        return RuleResponse.fromDomain(rule);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a rule by ID",
            description = "Retrieves a specific fraud detection rule by ID. " +
                    "Only returns the rule if it belongs to the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rule retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RuleResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context"),
            @ApiResponse(responseCode = "404", description = "Rule not found or does not belong to tenant")
    })
    public ResponseEntity<RuleResponse> getRule(
            @Parameter(description = "Rule ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id
    ) {
        String tenantId = getTenantId();
        log.debug("Getting rule {} for tenant: {}", id, tenantId);

        return ruleService.findById(tenantId, id)
                .map(rule -> ResponseEntity.ok(RuleResponse.fromDomain(rule)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(
            summary = "Get all rules for tenant",
            description = "Retrieves all fraud detection rules for the authenticated tenant. " +
                    "Only returns rules belonging to the tenant from the JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rules retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RuleResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public List<RuleResponse> getRules(
            @Parameter(description = "Filter by active status", example = "true")
            @RequestParam(required = false) Boolean active
    ) {
        String tenantId = getTenantId();
        log.debug("Getting rules for tenant: {}, active: {}", tenantId, active);

        List<AntiFraudRule> rules;
        if (active != null && active) {
            rules = ruleService.findActiveByTenant(tenantId);
        } else {
            rules = ruleService.findByTenant(tenantId);
        }

        return rules.stream()
                .map(RuleResponse::fromDomain)
                .toList();
    }

    @GetMapping("/active")
    @Operation(
            summary = "Get active rules ordered by priority",
            description = "Retrieves all active fraud detection rules for the authenticated tenant, " +
                    "ordered by priority (highest first). Used for fraud detection evaluation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Active rules retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RuleResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public List<RuleResponse> getActiveRulesByPriority() {
        String tenantId = getTenantId();
        log.debug("Getting active rules by priority for tenant: {}", tenantId);

        List<AntiFraudRule> rules = ruleService.findActiveByTenantOrderByPriority(tenantId);
        return rules.stream()
                .map(RuleResponse::fromDomain)
                .toList();
    }

    @GetMapping("/by-type/{ruleType}")
    @Operation(
            summary = "Get active rules by type",
            description = "Retrieves all active fraud detection rules of a specific type " +
                    "for the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rules retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RuleResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public List<RuleResponse> getActiveRulesByType(
            @Parameter(description = "Rule type", required = true, example = "THRESHOLD")
            @PathVariable AntiFraudRule.RuleType ruleType
    ) {
        String tenantId = getTenantId();
        log.debug("Getting active rules by type {} for tenant: {}", ruleType, tenantId);

        List<AntiFraudRule> rules = ruleService.findActiveByTenantAndType(tenantId, ruleType);
        return rules.stream()
                .map(RuleResponse::fromDomain)
                .toList();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a rule",
            description = "Updates an existing fraud detection rule. " +
                    "Only updates the rule if it belongs to the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rule updated successfully",
                    content = @Content(schema = @Schema(implementation = RuleResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context"),
            @ApiResponse(responseCode = "404", description = "Rule not found or does not belong to tenant")
    })
    public RuleResponse updateRule(
            @Parameter(description = "Rule ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id,
            @Parameter(description = "Rule update request", required = true)
            @Valid @RequestBody UpdateRuleRequest request
    ) {
        String tenantId = getTenantId();
        log.info("Updating rule {} for tenant: {}", id, tenantId);

        AntiFraudRuleService.UpdateRuleRequest serviceRequest =
                new AntiFraudRuleService.UpdateRuleRequest(
                        request.name(),
                        request.description(),
                        request.ruleType(),
                        request.active(),
                        request.priority(),
                        request.conditions(),
                        request.actions(),
                        getCurrentUser()
                );

        AntiFraudRule rule = ruleService.updateRule(tenantId, id, serviceRequest);
        return RuleResponse.fromDomain(rule);
    }

    @PatchMapping("/{id}/active")
    @Operation(
            summary = "Activate or deactivate a rule",
            description = "Sets the active status of a rule. " +
                    "Only updates the rule if it belongs to the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rule status updated successfully",
                    content = @Content(schema = @Schema(implementation = RuleResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context"),
            @ApiResponse(responseCode = "404", description = "Rule not found or does not belong to tenant")
    })
    public RuleResponse setRuleActive(
            @Parameter(description = "Rule ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id,
            @Parameter(description = "Active status", required = true, example = "false")
            @RequestParam boolean active
    ) {
        String tenantId = getTenantId();
        log.info("Setting active={} for rule {} in tenant: {}", active, id, tenantId);

        AntiFraudRule rule = ruleService.setActive(tenantId, id, active);
        return RuleResponse.fromDomain(rule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a rule",
            description = "Deletes a fraud detection rule. " +
                    "Only deletes the rule if it belongs to the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rule deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context"),
            @ApiResponse(responseCode = "404", description = "Rule not found or does not belong to tenant")
    })
    public void deleteRule(
            @Parameter(description = "Rule ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id
    ) {
        String tenantId = getTenantId();
        log.info("Deleting rule {} for tenant: {}", id, tenantId);

        ruleService.deleteRule(tenantId, id);
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Get rule statistics",
            description = "Returns statistics about fraud detection rules for the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public RuleStatsResponse getStats() {
        String tenantId = getTenantId();
        log.debug("Getting stats for tenant: {}", tenantId);

        long activeCount = ruleService.countActiveByTenant(tenantId);
        return new RuleStatsResponse(activeCount);
    }

    /**
     * Statistics response for rules.
     */
    public record RuleStatsResponse(
            @Schema(description = "Number of active rules", example = "15")
            long activeRuleCount
    ) {}
}
