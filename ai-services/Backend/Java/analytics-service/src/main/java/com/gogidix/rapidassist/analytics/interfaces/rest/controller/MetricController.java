package com.gogidix.rapidassist.analytics.interfaces.rest.controller;

import com.gogidix.rapidassist.analytics.application.dto.MetricDto;
import com.gogidix.rapidassist.analytics.application.service.MetricApplicationService;
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
 * REST Controller for Metric operations.
 * API endpoint: /api/v1/metrics
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics", description = "Metric collection APIs")
public class MetricController {

    private final MetricApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Create metric", description = "Creates a new metric entry")
    public ResponseEntity<MetricDto> createMetric(
            @Valid @RequestBody MetricDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Creating metric for tenant: {}", tenantId);
        MetricDto result = applicationService.createMetric(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/batch")
    @Operation(summary = "Batch create metrics", description = "Creates multiple metric entries")
    public ResponseEntity<List<MetricDto>> batchCreateMetrics(
            @Valid @RequestBody List<MetricDto> requests,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Batch creating {} metrics for tenant: {}", requests.size(), tenantId);
        List<MetricDto> results = applicationService.batchCreateMetrics(tenantId, requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(results);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get metric", description = "Retrieves metric by ID")
    public ResponseEntity<MetricDto> getMetric(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting metric: {} for tenant: {}", id, tenantId);
        MetricDto result = applicationService.getMetric(tenantId, id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Operation(summary = "Get all metrics", description = "Retrieves all metrics for a tenant")
    public ResponseEntity<List<MetricDto>> getMetricsByTenant(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting all metrics for tenant: {}", tenantId);
        List<MetricDto> results = applicationService.getMetricsByTenant(tenantId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/name/{metricName}")
    @Operation(summary = "Get metrics by name", description = "Retrieves metrics by name")
    public ResponseEntity<List<MetricDto>> getMetricsByName(
            @PathVariable String metricName,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting metrics by name: {} for tenant: {}", metricName, tenantId);
        List<MetricDto> results = applicationService.getMetricsByName(tenantId, metricName);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get metrics by category", description = "Retrieves metrics by category")
    public ResponseEntity<List<MetricDto>> getMetricsByCategory(
            @PathVariable String category,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting metrics by category: {} for tenant: {}", category, tenantId);
        List<MetricDto> results = applicationService.getMetricsByCategory(tenantId, category);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/timerange")
    @Operation(summary = "Get metrics by time range", description = "Retrieves metrics by time range")
    public ResponseEntity<List<MetricDto>> getMetricsByTimeRange(
            @Parameter(description = "Start time") @RequestParam LocalDateTime startTime,
            @Parameter(description = "End time") @RequestParam LocalDateTime endTime,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting metrics by time range for tenant: {}", tenantId);
        List<MetricDto> results = applicationService.getMetricsByTimeRange(tenantId, startTime, endTime);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete metric", description = "Deletes metric by ID")
    public ResponseEntity<Void> deleteMetric(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Deleting metric: {} for tenant: {}", id, tenantId);
        applicationService.deleteMetric(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}
