package com.gogidix.rapidassist.config.service.adapters.in.web;

import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.BulkUpdateRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.ConfigurationUpdateRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.CreateConfigurationRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.UpdateConfigurationRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.request.ValidateValueRequest;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.response.ValidationResult;
import com.gogidix.rapidassist.config.service.adapters.in.web.dto.response.ValueValidationResult;
import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import com.gogidix.rapidassist.config.service.domain.port.in.ConfigurationCommand;
import com.gogidix.rapidassist.config.service.domain.port.in.ConfigurationQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/configurations")
@Validated
@Tag(name = "Configuration", description = "Configuration management API for managing application configurations with multi-tenant support")
@SecurityRequirement(name = "bearer-jwt")
@SecurityRequirement(name = "basic-auth")
public class ConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    @Autowired
    private ConfigurationCommand configurationCommand;

    @Autowired
    private ConfigurationQuery configurationQuery;

    @PostMapping
    @PreAuthorize("hasRole('CONFIG_ADMIN') or @securityService.canManageConfiguration(#principal.username, #command.tenantId)")
    @Operation(
        summary = "Create a new configuration",
        description = "Creates a new configuration for the specified tenant, environment, and namespace. Requires CONFIG_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Configuration created successfully",
            content = @Content(schema = @Schema(implementation = Configuration.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "409", description = "Configuration already exists")
    })
    public CompletableFuture<ResponseEntity<Configuration>> createConfiguration(
        @Parameter(description = "Configuration creation request", required = true)
        @Valid @RequestBody CreateConfigurationRequest request,
        @Parameter(description = "Authenticated user details", hidden = true)
        @AuthenticationPrincipal UserDetails principal) {

        ConfigurationCommand.CreateConfigurationCommand command = new ConfigurationCommand.CreateConfigurationCommand(
            request.tenantId(),
            request.configKey(),
            request.environment(),
            request.namespace(),
            request.value(),
            request.dataType(),
            request.encrypted(),
            request.required(),
            request.defaultValue(),
            request.description(),
            request.tags(),
            request.metadata(),
            request.schema(),
            principal.getUsername(),
            request.reason()
        );

        return configurationCommand.createConfiguration(command)
            .thenApply(config -> {
                logger.info("Configuration created: {} for tenant: {}", request.configKey(), request.tenantId());
                return ResponseEntity.status(HttpStatus.CREATED).body(config);
            })
            .exceptionally(e -> {
                logger.error("Error creating configuration", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            });
    }

    @PutMapping("/{tenantId}/{configKey}/{environment}/{namespace}")
    @PreAuthorize("hasRole('CONFIG_ADMIN') or @securityService.canManageConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<Configuration>> updateConfiguration(
        @PathVariable @NotBlank String tenantId,
        @PathVariable @NotBlank String configKey,
        @PathVariable @NotBlank String environment,
        @PathVariable @NotBlank String namespace,
        @Valid @RequestBody UpdateConfigurationRequest request,
        @AuthenticationPrincipal UserDetails principal) {

        ConfigurationCommand.UpdateConfigurationCommand command = new ConfigurationCommand.UpdateConfigurationCommand(
            tenantId, configKey, environment, namespace,
            request.value(), principal.getUsername(), request.reason(), request.forceUpdate()
        );

        return configurationCommand.updateConfiguration(command)
            .thenApply(configOpt -> configOpt
                .map(config -> {
                    logger.info("Configuration updated: {} for tenant: {}", configKey, tenantId);
                    return ResponseEntity.ok(config);
                })
                .orElse(ResponseEntity.notFound().build()))
            .exceptionally(e -> {
                logger.error("Error updating configuration", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            });
    }

    @GetMapping("/{tenantId}/{configKey}/{environment}/{namespace}")
    @PreAuthorize("hasRole('CONFIG_READER') or @securityService.canReadConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<Configuration>> getConfiguration(
        @PathVariable @NotBlank String tenantId,
        @PathVariable @NotBlank String configKey,
        @PathVariable @NotBlank String environment,
        @PathVariable @NotBlank String namespace,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationQuery.getConfiguration(tenantId, configKey, environment, namespace)
            .thenApply(configOpt -> configOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()))
            .exceptionally(e -> {
                logger.error("Error getting configuration", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GetMapping("/namespace/{tenantId}/{environment}/{namespace}")
    @PreAuthorize("hasRole('CONFIG_READER') or @securityService.canReadConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<List<Configuration>>> getConfigurationsByNamespace(
        @PathVariable @NotBlank String tenantId,
        @PathVariable @NotBlank String environment,
        @PathVariable @NotBlank String namespace,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationQuery.getConfigurationsByNamespace(tenantId, environment, namespace)
            .thenApply(ResponseEntity::ok)
            .exceptionally(e -> {
                logger.error("Error getting configurations by namespace", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasRole('CONFIG_READER') or @securityService.canReadConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<List<Configuration>>> getConfigurationsByTenant(
        @PathVariable @NotBlank String tenantId,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationQuery.getConfigurationsByTenant(tenantId)
            .thenApply(ResponseEntity::ok)
            .exceptionally(e -> {
                logger.error("Error getting configurations by tenant", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }

    @DeleteMapping("/{tenantId}/{configKey}/{environment}/{namespace}")
    @PreAuthorize("hasRole('CONFIG_ADMIN') or @securityService.canManageConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<Void>> deleteConfiguration(
        @PathVariable @NotBlank String tenantId,
        @PathVariable @NotBlank String configKey,
        @PathVariable @NotBlank String environment,
        @PathVariable @NotBlank String namespace,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationCommand.deleteConfiguration(tenantId, configKey, environment, namespace, principal.getUsername())
            .<ResponseEntity<Void>>thenApply(deleted -> {
                if (deleted) {
                    logger.info("Configuration deleted: {} for tenant: {}", configKey, tenantId);
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            })
            .exceptionally(e -> {
                logger.error("Error deleting configuration", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            });
    }

    @PostMapping("/rollback/{tenantId}/{configKey}/{environment}/{namespace}")
    @PreAuthorize("hasRole('CONFIG_ADMIN') or @securityService.canManageConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<Configuration>> rollbackConfiguration(
        @PathVariable @NotBlank String tenantId,
        @PathVariable @NotBlank String configKey,
        @PathVariable @NotBlank String environment,
        @PathVariable @NotBlank String namespace,
        @RequestParam @NotNull Integer targetVersion,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationCommand.rollbackConfiguration(
            tenantId, configKey, environment, namespace, targetVersion, principal.getUsername()
        ).thenApply(configOpt -> configOpt
            .map(config -> {
                logger.info("Configuration rolled back: {} to version: {} for tenant: {}",
                          configKey, targetVersion, tenantId);
                return ResponseEntity.ok(config);
            })
            .orElse(ResponseEntity.notFound().build()))
            .exceptionally(e -> {
                logger.error("Error rolling back configuration", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            });
    }

    @GetMapping("/history/{tenantId}/{configKey}/{environment}")
    @PreAuthorize("hasRole('CONFIG_READER') or @securityService.canReadConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<List<Configuration>>> getConfigurationHistory(
        @PathVariable @NotBlank String tenantId,
        @PathVariable @NotBlank String configKey,
        @PathVariable @NotBlank String environment,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationQuery.getConfigurationHistory(tenantId, configKey, environment)
            .thenApply(ResponseEntity::ok)
            .exceptionally(e -> {
                logger.error("Error getting configuration history", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GetMapping("/changes/{tenantId}/{configKey}/{environment}/{namespace}")
    @PreAuthorize("hasRole('CONFIG_READER') or @securityService.canReadConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<List<ConfigurationChange>>> getConfigurationChanges(
        @PathVariable @NotBlank String tenantId,
        @PathVariable @NotBlank String configKey,
        @PathVariable @NotBlank String environment,
        @PathVariable @NotBlank String namespace,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationQuery.getConfigurationChanges(tenantId, configKey, environment, namespace)
            .thenApply(ResponseEntity::ok)
            .exceptionally(e -> {
                logger.error("Error getting configuration changes", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GetMapping("/pending-approvals/{tenantId}")
    @PreAuthorize("hasRole('CONFIG_APPROVER') or @securityService.canApproveConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<List<ConfigurationChange>>> getPendingApprovals(
        @PathVariable @NotBlank String tenantId,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationQuery.getPendingApprovals(tenantId)
            .thenApply(ResponseEntity::ok)
            .exceptionally(e -> {
                logger.error("Error getting pending approvals", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }

    @PostMapping("/approve/{changeId}")
    @PreAuthorize("hasRole('CONFIG_APPROVER') or hasRole('CONFIG_ADMIN')")
    public CompletableFuture<ResponseEntity<ConfigurationChange>> approveConfigurationChange(
        @PathVariable @NotBlank String changeId,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationCommand.approveConfigurationChange(changeId, principal.getUsername())
            .thenApply(change -> {
                logger.info("Configuration change approved: {}", changeId);
                return ResponseEntity.ok(change);
            })
            .exceptionally(e -> {
                logger.error("Error approving configuration change", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            });
    }

    @PostMapping("/reject/{changeId}")
    @PreAuthorize("hasRole('CONFIG_APPROVER') or hasRole('CONFIG_ADMIN')")
    public CompletableFuture<ResponseEntity<ConfigurationChange>> rejectConfigurationChange(
        @PathVariable @NotBlank String changeId,
        @RequestBody Map<String, String> request,
        @AuthenticationPrincipal UserDetails principal) {

        String reason = request.getOrDefault("reason", "No reason provided");
        return configurationCommand.rejectConfigurationChange(changeId, principal.getUsername(), reason)
            .thenApply(change -> {
                logger.info("Configuration change rejected: {}", changeId);
                return ResponseEntity.ok(change);
            })
            .exceptionally(e -> {
                logger.error("Error rejecting configuration change", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            });
    }

    @PostMapping("/bulk-update")
    @PreAuthorize("hasRole('CONFIG_ADMIN') or @securityService.canManageConfiguration(#principal.username, #request.tenantId)")
    public CompletableFuture<ResponseEntity<List<Configuration>>> bulkUpdateConfigurations(
        @Valid @RequestBody BulkUpdateRequest request,
        @AuthenticationPrincipal UserDetails principal) {

        List<ConfigurationCommand.ConfigurationUpdate> updates = request.updates().stream()
            .map(update -> new ConfigurationCommand.ConfigurationUpdate(
                update.configKey(), update.environment(), update.namespace(), update.value()
            ))
            .toList();

        ConfigurationCommand.BulkUpdateCommand command = new ConfigurationCommand.BulkUpdateCommand(
            request.tenantId(), updates, principal.getUsername(), request.reason()
        );

        return configurationCommand.bulkUpdateConfigurations(command)
            .thenApply(configs -> {
                logger.info("Bulk updated {} configurations for tenant: {}", configs.size(), request.tenantId());
                return ResponseEntity.ok(configs);
            })
            .exceptionally(e -> {
                logger.error("Error bulk updating configurations", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            });
    }

    @PostMapping("/validate/{tenantId}/{environment}")
    @PreAuthorize("hasRole('CONFIG_ADMIN') or @securityService.canManageConfiguration(#principal.username, #tenantId)")
    public CompletableFuture<ResponseEntity<ValidationResult>> validateConfigurations(
        @PathVariable @NotBlank String tenantId,
        @PathVariable @NotBlank String environment,
        @AuthenticationPrincipal UserDetails principal) {

        return configurationCommand.validateConfigurations(tenantId, environment)
            .thenApply(valid -> ResponseEntity.ok(new ValidationResult(valid, Instant.now())))
            .exceptionally(e -> {
                logger.error("Error validating configurations", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }

    @PostMapping("/validate-value")
    @PreAuthorize("hasRole('CONFIG_READER')")
    public CompletableFuture<ResponseEntity<ValueValidationResult>> validateValue(
        @RequestBody ValidateValueRequest request) {

        return configurationQuery.validateConfigurationValue(request.value(), request.schema())
            .thenApply(valid -> ResponseEntity.ok(new ValueValidationResult(valid, Set.of())))
            .exceptionally(e -> {
                logger.error("Error validating value", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            });
    }
}