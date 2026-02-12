package com.gogidix.rapidassist.analytics.interfaces.rest.controller;

import com.gogidix.rapidassist.analytics.application.dto.AnalyticsDto;
import com.gogidix.rapidassist.analytics.application.service.AnalyticsApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Analytics operations.
 * API endpoint: /api/v1/analytics
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics APIs")
public class AnalyticsController {

    private final AnalyticsApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Create analytics", description = "Creates a new analytics entry")
    public ResponseEntity<AnalyticsDto> createAnalytics(
            @Valid @RequestBody AnalyticsDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Creating analytics for tenant: {}", tenantId);
        AnalyticsDto result = applicationService.createAnalytics(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get analytics", description = "Retrieves analytics by ID")
    public ResponseEntity<AnalyticsDto> getAnalytics(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting analytics: {} for tenant: {}", id, tenantId);
        AnalyticsDto result = applicationService.getAnalytics(tenantId, id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Operation(summary = "Get all analytics", description = "Retrieves all analytics for a tenant")
    public ResponseEntity<List<AnalyticsDto>> getAnalyticsByTenant(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting all analytics for tenant: {}", tenantId);
        List<AnalyticsDto> results = applicationService.getAnalyticsByTenant(tenantId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get analytics by status", description = "Retrieves analytics by status")
    public ResponseEntity<List<AnalyticsDto>> getAnalyticsByStatus(
            @PathVariable String status,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting analytics by status: {} for tenant: {}", status, tenantId);
        List<AnalyticsDto> results = applicationService.getAnalyticsByStatus(tenantId, status);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/type/{analyticsType}")
    @Operation(summary = "Get analytics by type", description = "Retrieves analytics by type")
    public ResponseEntity<List<AnalyticsDto>> getAnalyticsByType(
            @PathVariable String analyticsType,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting analytics by type: {} for tenant: {}", analyticsType, tenantId);
        List<AnalyticsDto> results = applicationService.getAnalyticsByType(tenantId, analyticsType);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/timerange")
    @Operation(summary = "Get analytics by time range", description = "Retrieves analytics by time range")
    public ResponseEntity<List<AnalyticsDto>> getAnalyticsByTimeRange(
            @Parameter(description = "Start time") @RequestParam LocalDateTime startTime,
            @Parameter(description = "End time") @RequestParam LocalDateTime endTime,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting analytics by time range for tenant: {}", tenantId);
        List<AnalyticsDto> results = applicationService.getAnalyticsByTimeRange(tenantId, startTime, endTime);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{id}/compute")
    @Operation(summary = "Compute analytics", description = "Triggers computation for analytics")
    public ResponseEntity<AnalyticsDto> computeAnalytics(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Computing analytics: {} for tenant: {}", id, tenantId);
        AnalyticsDto result = applicationService.computeAnalytics(tenantId, id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete analytics", description = "Deletes analytics by ID")
    public ResponseEntity<Void> deleteAnalytics(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Deleting analytics: {} for tenant: {}", id, tenantId);
        applicationService.deleteAnalytics(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}
