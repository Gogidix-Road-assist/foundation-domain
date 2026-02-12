package com.gogidix.rapidassist.ai.gateway.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.gateway.application.dto.*;
import com.gogidix.rapidassist.ai.gateway.application.service.GatewayApplicationService;
import com.gogidix.rapidassist.ai.gateway.domain.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for AI Gateway operations.
 * API endpoint: /api/v1/gateway
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/gateway")
@RequiredArgsConstructor
@Tag(name = "AI Gateway", description = "AI Gateway Management APIs")
public class GatewayRestController {

    private final GatewayApplicationService applicationService;

    // ============================================================
    // Gateway Config Endpoints
    // ============================================================

    @PostMapping("/configs")
    @Operation(summary = "Create gateway config", description = "Creates a new gateway configuration")
    public ResponseEntity<GatewayConfigDto> createGatewayConfig(
            @Valid @RequestBody GatewayConfigDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        request.setTenantId(tenantId);

        GatewayConfigDto saved = applicationService.createGatewayConfig(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/configs/{configId}")
    @Operation(summary = "Get gateway config", description = "Retrieves a gateway configuration by ID")
    public ResponseEntity<GatewayConfigDto> getGatewayConfig(
            @PathVariable UUID configId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        GatewayConfigDto config = applicationService.getGatewayConfig(configId, tenantId);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/configs")
    @Operation(summary = "Get all gateway configs", description = "Retrieves all gateway configurations for a tenant")
    public ResponseEntity<List<GatewayConfigDto>> getAllGatewayConfigs(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<GatewayConfigDto> configs = applicationService.getAllGatewayConfigs(tenantId);
        return ResponseEntity.ok(configs);
    }

    @PutMapping("/configs/{configId}")
    @Operation(summary = "Update gateway config", description = "Updates a gateway configuration")
    public ResponseEntity<GatewayConfigDto> updateGatewayConfig(
            @PathVariable UUID configId,
            @Valid @RequestBody GatewayConfigDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        GatewayConfigDto updated = applicationService.updateGatewayConfig(configId, tenantId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/configs/{configId}")
    @Operation(summary = "Delete gateway config", description = "Deletes a gateway configuration")
    public ResponseEntity<Void> deleteGatewayConfig(
            @PathVariable UUID configId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.deleteGatewayConfig(configId, tenantId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // Route Endpoints
    // ============================================================

    @PostMapping("/routes")
    @Operation(summary = "Create route", description = "Creates a new route configuration")
    public ResponseEntity<RouteDto> createRoute(
            @Valid @RequestBody RouteDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        request.setTenantId(tenantId);

        RouteDto saved = applicationService.createRoute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/routes/{routeId}")
    @Operation(summary = "Get route", description = "Retrieves a route by ID")
    public ResponseEntity<RouteDto> getRoute(
            @PathVariable UUID routeId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        RouteDto route = applicationService.getRoute(routeId, tenantId);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/routes")
    @Operation(summary = "Get all routes", description = "Retrieves all routes for a tenant")
    public ResponseEntity<List<RouteDto>> getAllRoutes(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<RouteDto> routes = applicationService.getAllRoutes(tenantId);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/routes/active")
    @Operation(summary = "Get active routes", description = "Retrieves all active routes for a tenant")
    public ResponseEntity<List<RouteDto>> getActiveRoutes(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<RouteDto> routes = applicationService.getActiveRoutes(tenantId);
        return ResponseEntity.ok(routes);
    }

    @PutMapping("/routes/{routeId}")
    @Operation(summary = "Update route", description = "Updates a route configuration")
    public ResponseEntity<RouteDto> updateRoute(
            @PathVariable UUID routeId,
            @Valid @RequestBody RouteDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        RouteDto updated = applicationService.updateRoute(routeId, tenantId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/routes/{routeId}")
    @Operation(summary = "Delete route", description = "Deletes a route")
    public ResponseEntity<Void> deleteRoute(
            @PathVariable UUID routeId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.deleteRoute(routeId, tenantId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // API Key Endpoints
    // ============================================================

    @PostMapping("/api-keys")
    @Operation(summary = "Create API key", description = "Creates a new API key")
    public ResponseEntity<ApiKeyDto> createApiKey(
            @Valid @RequestBody ApiKeyDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        request.setTenantId(tenantId);

        ApiKeyDto saved = applicationService.createApiKey(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/api-keys/{keyId}")
    @Operation(summary = "Get API key", description = "Retrieves an API key by ID")
    public ResponseEntity<ApiKeyDto> getApiKey(
            @PathVariable UUID keyId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        ApiKeyDto apiKey = applicationService.getApiKey(keyId, tenantId);
        return ResponseEntity.ok(apiKey);
    }

    @GetMapping("/api-keys")
    @Operation(summary = "Get all API keys", description = "Retrieves all API keys for a tenant")
    public ResponseEntity<List<ApiKeyDto>> getAllApiKeys(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<ApiKeyDto> apiKeys = applicationService.getAllApiKeys(tenantId);
        return ResponseEntity.ok(apiKeys);
    }

    @PutMapping("/api-keys/{keyId}")
    @Operation(summary = "Update API key", description = "Updates an API key")
    public ResponseEntity<ApiKeyDto> updateApiKey(
            @PathVariable UUID keyId,
            @Valid @RequestBody ApiKeyDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        ApiKeyDto updated = applicationService.updateApiKey(keyId, tenantId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api-keys/{keyId}")
    @Operation(summary = "Delete API key", description = "Deletes an API key")
    public ResponseEntity<Void> deleteApiKey(
            @PathVariable UUID keyId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.deleteApiKey(keyId, tenantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api-keys/{keyId}/revoke")
    @Operation(summary = "Revoke API key", description = "Revokes an API key")
    public ResponseEntity<Void> revokeApiKey(
            @PathVariable UUID keyId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.revokeApiKey(keyId, tenantId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // Rate Limit Endpoints
    // ============================================================

    @PostMapping("/rate-limits")
    @Operation(summary = "Create rate limit", description = "Creates a new rate limit configuration")
    public ResponseEntity<RateLimitDto> createRateLimit(
            @Valid @RequestBody RateLimitDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        request.setTenantId(tenantId);

        RateLimitDto saved = applicationService.createRateLimit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/rate-limits/{limitId}")
    @Operation(summary = "Get rate limit", description = "Retrieves a rate limit by ID")
    public ResponseEntity<RateLimitDto> getRateLimit(
            @PathVariable UUID limitId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        RateLimitDto rateLimit = applicationService.getRateLimit(limitId, tenantId);
        return ResponseEntity.ok(rateLimit);
    }

    @GetMapping("/rate-limits")
    @Operation(summary = "Get all rate limits", description = "Retrieves all rate limits for a tenant")
    public ResponseEntity<List<RateLimitDto>> getAllRateLimits(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<RateLimitDto> rateLimits = applicationService.getAllRateLimits(tenantId);
        return ResponseEntity.ok(rateLimits);
    }

    @PutMapping("/rate-limits/{limitId}")
    @Operation(summary = "Update rate limit", description = "Updates a rate limit configuration")
    public ResponseEntity<RateLimitDto> updateRateLimit(
            @PathVariable UUID limitId,
            @Valid @RequestBody RateLimitDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        RateLimitDto updated = applicationService.updateRateLimit(limitId, tenantId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/rate-limits/{limitId}")
    @Operation(summary = "Delete rate limit", description = "Deletes a rate limit")
    public ResponseEntity<Void> deleteRateLimit(
            @PathVariable UUID limitId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.deleteRateLimit(limitId, tenantId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // Request Log Endpoints
    // ============================================================

    @PostMapping("/logs")
    @Operation(summary = "Log request", description = "Logs a request to the gateway")
    public ResponseEntity<RequestLogDto> logRequest(
            @Valid @RequestBody RequestLogDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        request.setTenantId(tenantId);

        RequestLogDto saved = applicationService.logRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/logs")
    @Operation(summary = "Get request logs", description = "Retrieves request logs for a tenant")
    public ResponseEntity<List<RequestLogDto>> getRequestLogs(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Limit number of results") @RequestParam(defaultValue = "100") Integer limit) {

        TenantContext.setTenantId(tenantId);

        List<RequestLogDto> logs = applicationService.getRequestLogs(tenantId, limit);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/service/{serviceId}")
    @Operation(summary = "Get logs by service", description = "Retrieves request logs for a specific service")
    public ResponseEntity<List<RequestLogDto>> getRequestLogsByService(
            @PathVariable String serviceId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Limit number of results") @RequestParam(defaultValue = "100") Integer limit) {

        TenantContext.setTenantId(tenantId);

        List<RequestLogDto> logs = applicationService.getRequestLogsByService(tenantId, serviceId, limit);
        return ResponseEntity.ok(logs);
    }

    @DeleteMapping("/logs/cleanup")
    @Operation(summary = "Cleanup old logs", description = "Deletes request logs older than specified days")
    public ResponseEntity<Void> cleanupOldLogs(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Days to keep") @RequestParam(defaultValue = "30") Integer daysToKeep) {

        TenantContext.setTenantId(tenantId);

        applicationService.cleanupOldLogs(tenantId, daysToKeep);
        return ResponseEntity.noContent().build();
    }
}
