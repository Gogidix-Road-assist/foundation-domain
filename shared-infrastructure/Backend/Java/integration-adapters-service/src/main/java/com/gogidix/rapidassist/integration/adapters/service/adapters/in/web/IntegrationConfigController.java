package com.gogidix.rapidassist.integration.adapters.service.adapters.in.web;

import com.gogidix.rapidassist.integration.adapters.service.application.dto.CreateIntegrationConfigRequest;
import com.gogidix.rapidassist.integration.adapters.service.application.dto.IntegrationConfigResponse;
import com.gogidix.rapidassist.integration.adapters.service.application.dto.UpdateIntegrationConfigRequest;
import com.gogidix.rapidassist.integration.adapters.service.application.service.IntegrationConfigService;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/integration-configs")
@Tag(name = "Integration Configurations", description = "Third-party integration configuration management endpoints")
public class IntegrationConfigController {

    private final IntegrationConfigService integrationConfigService;

    public IntegrationConfigController(IntegrationConfigService integrationConfigService) {
        this.integrationConfigService = integrationConfigService;
    }

    protected String getTenantId() {
        return RequestContextHolder.get()
                .map(c -> c.tenantId())
                .orElseThrow(() -> new IllegalStateException("TenantId required"));
    }

    @GetMapping
    @Operation(
            summary = "Get all integration configurations for tenant",
            description = "Retrieves all integration configurations for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integration configurations retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<IntegrationConfigResponse>> getAllConfigs() {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.findByTenant(tenantId));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get integration configuration by ID",
            description = "Retrieves a specific integration configuration by ID for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integration configuration retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Integration configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationConfigResponse> getConfigById(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.findByTenantAndId(tenantId, id));
    }

    @GetMapping("/provider/{provider}")
    @Operation(
            summary = "Get integration configuration by provider",
            description = "Retrieves integration configuration filtered by provider for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integration configuration retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Integration configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationConfigResponse> getConfigByProvider(
            @Parameter(description = "Provider name", required = true)
            @PathVariable String provider) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.findByTenantAndProvider(tenantId, provider));
    }

    @GetMapping("/enabled")
    @Operation(
            summary = "Get enabled integration configurations",
            description = "Retrieves all enabled integration configurations for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enabled integration configurations retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<IntegrationConfigResponse>> getEnabledConfigs() {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.findEnabledByTenant(tenantId));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search integration configurations",
            description = "Searches integration configurations by provider or provider name for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<IntegrationConfigResponse>> searchConfigs(
            @Parameter(description = "Search term", required = true)
            @RequestParam String search) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.searchByTenant(tenantId, search));
    }

    @GetMapping("/stats/count")
    @Operation(
            summary = "Get integration configurations count",
            description = "Returns the count of integration configurations for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> getConfigsCount() {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.countByTenant(tenantId));
    }

    @PostMapping
    @Operation(
            summary = "Create integration configuration",
            description = "Creates a new integration configuration for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Integration configuration created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationConfigResponse> createConfig(
            @Valid @RequestBody CreateIntegrationConfigRequest request) {
        String tenantId = getTenantId();
        IntegrationConfigResponse response = integrationConfigService.create(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update integration configuration",
            description = "Updates an existing integration configuration for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integration configuration updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Integration configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationConfigResponse> updateConfig(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody UpdateIntegrationConfigRequest request) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.update(tenantId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete integration configuration",
            description = "Deletes an integration configuration for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Integration configuration deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Integration configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteConfig(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        String tenantId = getTenantId();
        integrationConfigService.delete(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enable")
    @Operation(
            summary = "Enable integration configuration",
            description = "Enables an integration configuration for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integration configuration enabled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Integration configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationConfigResponse> enableConfig(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.enable(tenantId, id));
    }

    @PostMapping("/{id}/disable")
    @Operation(
            summary = "Disable integration configuration",
            description = "Disables an integration configuration for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integration configuration disabled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Integration configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationConfigResponse> disableConfig(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.disable(tenantId, id));
    }

    @PostMapping("/{id}/test-connection")
    @Operation(
            summary = "Test integration connection",
            description = "Tests the connection for an integration configuration for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connection test completed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Integration configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationConfigResponse> testConnection(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.testConnection(tenantId, id));
    }

    @PutMapping("/{id}/sync-status")
    @Operation(
            summary = "Update sync status",
            description = "Updates the synchronization status for an integration configuration for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sync status updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Integration configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationConfigResponse> updateSyncStatus(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id,
            @Parameter(description = "Sync status", required = true)
            @RequestParam String status) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(integrationConfigService.updateSyncStatus(tenantId, id, status));
    }
}
