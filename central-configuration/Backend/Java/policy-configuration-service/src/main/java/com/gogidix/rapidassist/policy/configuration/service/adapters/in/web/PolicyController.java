package com.gogidix.rapidassist.policy.configuration.service.adapters.in.web;

import com.gogidix.rapidassist.policy.configuration.service.application.PolicyService;
import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;
import com.gogidix.rapidassist.policy.configuration.service.domain.port.in.PolicyCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for Policy Configuration Management
 * Provides CRUD operations for organizational policies
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/policies")
@Validated
@Tag(name = "Policy Configuration", description = "APIs for managing organizational policies including security, privacy, and business rules")
@SecurityRequirement(name = "bearerAuth")
public class PolicyController {

    private final PolicyCommand commandService;
    private final PolicyService queryService;

    public PolicyController(PolicyService policyService) {
        this.commandService = policyService;
        this.queryService = policyService;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint", description = "Returns the health status of the policy configuration service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy",
                    content = @Content(schema = @Schema(implementation = HealthResponse.class)))
    })
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "policy-configuration-service"));
    }

    @GetMapping
    @Operation(summary = "Get all policies", description = "Retrieve policies by tenant ID, optionally filtered by type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policies retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public ResponseEntity<List<Policy>> getPolicies(
            @Parameter(description = "Tenant ID", required = true, example = "tenant-123")
            @RequestParam @NotBlank String tenantId,
            @Parameter(description = "Optional policy type filter", example = "SECURITY")
            @RequestParam(required = false) Policy.PolicyType type) {
        return ResponseEntity.ok(type != null
            ? queryService.getPoliciesByType(tenantId, type).join()
            : queryService.getPoliciesByTenant(tenantId).join());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active policies", description = "Retrieve all active policies for a tenant in a specific environment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active policies retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<Policy>> getActivePolicies(
            @Parameter(description = "Tenant ID", required = true, example = "tenant-123")
            @RequestParam @NotBlank String tenantId,
            @Parameter(description = "Environment (e.g., production, staging)", example = "production")
            @RequestParam(defaultValue = "production") String environment) {
        return ResponseEntity.ok(queryService.getActivePolicies(tenantId, environment).join());
    }

    @GetMapping("/{policyId}")
    @Operation(summary = "Get policy by ID", description = "Retrieve a specific policy by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Policy> getPolicy(
            @Parameter(description = "Policy ID", required = true, example = "pol-123")
            @PathVariable String policyId) {
        return queryService.getPolicyById(policyId).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new policy", description = "Create a new policy configuration for a tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Policy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to create policies")
    })
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {
        PolicyCommand.CreatePolicyCommand command = new PolicyCommand.CreatePolicyCommand(
            request.tenantId(), request.policyKey(), request.name(), request.description(),
            request.type(), request.scope(), request.rules(), request.constraints(),
            request.priority(), request.environment(), request.tags(), request.createdBy());
        Policy policy = commandService.createPolicy(command).join();
        return ResponseEntity.created(URI.create("/api/policies/" + policy.id())).body(policy);
    }

    @PutMapping("/{policyId}")
    @Operation(summary = "Update a policy", description = "Update an existing policy configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Policy not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public ResponseEntity<Policy> updatePolicy(
            @Parameter(description = "Policy ID", required = true, example = "pol-123")
            @PathVariable String policyId,
            @RequestBody UpdatePolicyRequest request) {
        return commandService.updatePolicy(policyId, request.toCommand()).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{policyId}/activate")
    @Operation(summary = "Activate a policy", description = "Activate a policy and mark it as active")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy activated successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Policy> activatePolicy(
            @Parameter(description = "Policy ID", required = true, example = "pol-123")
            @PathVariable String policyId,
            @RequestBody Map<String, String> request) {
        return commandService.activatePolicy(policyId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{policyId}/enforce")
    @Operation(summary = "Enforce or unenforce a policy", description = "Set whether a policy is enforced or not")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy enforcement updated successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Policy> enforcePolicy(
            @Parameter(description = "Policy ID", required = true, example = "pol-123")
            @PathVariable String policyId,
            @RequestBody EnforceRequest request) {
        return commandService.enforcePolicy(policyId, request.enforced(), request.updatedBy()).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{policyId}")
    @Operation(summary = "Delete a policy", description = "Delete a policy by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Policy deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to delete policies")
    })
    public ResponseEntity<Void> deletePolicy(
            @Parameter(description = "Policy ID", required = true, example = "pol-123")
            @PathVariable String policyId) {
        boolean deleted = commandService.deletePolicy(policyId).join();
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Request DTO for creating a new policy
     */
    @Schema(description = "Request body for creating a new policy")
    record CreatePolicyRequest(
            @Schema(description = "Tenant ID", example = "tenant-123", required = true)
            @NotBlank String tenantId,
            @Schema(description = "Unique policy key within the tenant", example = "MAX_LOGIN_ATTEMPTS", required = true)
            @NotBlank String policyKey,
            @Schema(description = "Policy name", example = "Maximum Login Attempts Policy", required = true)
            @NotBlank String name,
            @Schema(description = "Policy description", example = "Limits the number of failed login attempts")
            String description,
            @Schema(description = "Policy type", example = "SECURITY", required = true)
            @NotBlank Policy.PolicyType type,
            @Schema(description = "Policy scope defining where it applies", required = true)
            @NotBlank Policy.PolicyScope scope,
            @Schema(description = "Policy rules as key-value pairs", example = "{\"maxAttempts\": 5, \"lockoutDurationMinutes\": 30}")
            @NotBlank java.util.Map<String, Object> rules,
            @Schema(description = "Policy constraints", required = true)
            @NotBlank Policy.PolicyConstraints constraints,
            @Schema(description = "Policy priority (higher = more important)", example = "10")
            int priority,
            @Schema(description = "Environment", example = "production")
            @NotBlank String environment,
            @Schema(description = "Policy tags for categorization", example = "[\"authentication\", \"security\"]")
            Set<String> tags,
            @Schema(description = "User who created the policy", example = "admin@example.com", required = true)
            @NotBlank String createdBy
    ) {}

    /**
     * Request DTO for updating an existing policy
     */
    @Schema(description = "Request body for updating an existing policy")
    record UpdatePolicyRequest(
            @Schema(description = "Updated policy name", example = "Updated Login Attempts Policy")
            String name,
            @Schema(description = "Updated policy description", example = "Updated description")
            String description,
            @Schema(description = "Updated policy scope")
            Policy.PolicyScope scope,
            @Schema(description = "Updated policy rules")
            java.util.Map<String, Object> rules,
            @Schema(description = "Updated policy constraints")
            Policy.PolicyConstraints constraints,
            @Schema(description = "Updated priority", example = "15")
            int priority,
            @Schema(description = "User who updated the policy", example = "admin@example.com", required = true)
            @NotBlank String updatedBy
    ) {
        public PolicyCommand.UpdatePolicyCommand toCommand() {
            return new PolicyCommand.UpdatePolicyCommand(name, description, scope, rules, constraints, priority, updatedBy);
        }
    }

    /**
     * Request DTO for enforcing/unenforcing a policy
     */
    @Schema(description = "Request body for toggling policy enforcement")
    record EnforceRequest(
            @Schema(description = "Whether to enforce the policy", example = "true", required = true)
            boolean enforced,
            @Schema(description = "User who changed the enforcement status", example = "admin@example.com", required = true)
            @NotBlank String updatedBy
    ) {}

    /**
     * Health response schema
     */
    @Schema(description = "Health check response")
    record HealthResponse(
            @Schema(description = "Service status", example = "UP")
            String status,
            @Schema(description = "Service name", example = "policy-configuration-service")
            String service
    ) {}
}
