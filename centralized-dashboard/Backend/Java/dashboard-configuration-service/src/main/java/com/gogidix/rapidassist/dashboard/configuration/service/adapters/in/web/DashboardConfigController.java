package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web;

import com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto.*;
import com.gogidix.rapidassist.dashboard.configuration.service.application.DashboardConfigService;
import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboards")
@Tag(name = "Dashboard Configuration", description = "APIs for managing dashboard configurations, layouts, widgets, and permissions")
@SecurityRequirement(name = "bearerAuth")
public class DashboardConfigController {

    private final DashboardConfigService dashboardService;

    public DashboardConfigController(DashboardConfigService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/health")
    @Operation(
        summary = "Health check endpoint",
        description = "Returns the health status of the dashboard configuration service",
        tags = {"health"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Service is healthy",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "dashboard-configuration-service"
        ));
    }

    @GetMapping
    public ResponseEntity<List<DashboardConfiguration>> getAllDashboards() {
        return ResponseEntity.ok(dashboardService.getAllDashboards().join());
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<DashboardConfiguration>> getDashboardsByTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(dashboardService.getDashboardsByTenant(tenantId).join());
    }

    @GetMapping("/tenant/{tenantId}/active")
    public ResponseEntity<List<DashboardConfiguration>> getActiveDashboards(@PathVariable String tenantId) {
        return ResponseEntity.ok(dashboardService.getActiveDashboards(tenantId).join());
    }

    @GetMapping("/{dashboardId}")
    @Operation(
        summary = "Get dashboard by ID",
        description = "Retrieves a specific dashboard configuration by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard found"),
        @ApiResponse(responseCode = "404", description = "Dashboard not found")
    })
    public ResponseEntity<DashboardConfiguration> getDashboard(
        @Parameter(description = "Dashboard ID", required = true, example = "dash-001")
        @PathVariable String dashboardId) {
        return dashboardService.getDashboard(dashboardId).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<DashboardConfiguration>> getDashboardsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(dashboardService.getDashboardsByCategory(category).join());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DashboardConfiguration>> searchDashboards(
        @RequestParam String tenantId,
        @RequestParam String keyword) {
        return ResponseEntity.ok(dashboardService.searchDashboards(tenantId, keyword).join());
    }

    @PostMapping
    @Operation(
        summary = "Create a new dashboard",
        description = "Creates a new dashboard configuration with the specified details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dashboard created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DashboardConfiguration> createDashboard(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dashboard creation request",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateDashboardRequestDto.class))
        )
        @Valid @RequestBody CreateDashboardRequestDto request) {
        DashboardConfiguration dashboard = dashboardService.createDashboard(
            request.tenantId(),
            request.dashboardId(),
            request.name(),
            request.description(),
            request.createdBy()
        ).join();
        return ResponseEntity.created(URI.create("/api/dashboards/" + dashboard.dashboardId())).body(dashboard);
    }

    @PutMapping("/{dashboardId}")
    public ResponseEntity<DashboardConfiguration> updateDashboard(
        @PathVariable String dashboardId,
        @Valid @RequestBody UpdateDashboardRequestDto request) {
        return dashboardService.updateDashboard(dashboardId, request.name(), request.description(), request.updatedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{dashboardId}/layout")
    public ResponseEntity<DashboardConfiguration> updateLayout(
        @PathVariable String dashboardId,
        @Valid @RequestBody UpdateLayoutRequestDto request) {
        return dashboardService.updateLayout(dashboardId, request.layout(), request.updatedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{dashboardId}/widgets")
    public ResponseEntity<DashboardConfiguration> addWidget(
        @PathVariable String dashboardId,
        @Valid @RequestBody AddWidgetRequestDto request) {
        return dashboardService.addWidget(dashboardId, request.widget(), request.updatedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{dashboardId}/widgets/{widgetId}")
    public ResponseEntity<DashboardConfiguration> updateWidget(
        @PathVariable String dashboardId,
        @PathVariable String widgetId,
        @Valid @RequestBody UpdateWidgetRequestDto request) {
        return dashboardService.updateWidget(dashboardId, widgetId, request.widget(), request.updatedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{dashboardId}/widgets/{widgetId}")
    public ResponseEntity<DashboardConfiguration> removeWidget(
        @PathVariable String dashboardId,
        @PathVariable String widgetId,
        @RequestBody Map<String, String> request) {
        return dashboardService.removeWidget(dashboardId, widgetId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{dashboardId}/theme")
    public ResponseEntity<DashboardConfiguration> updateTheme(
        @PathVariable String dashboardId,
        @Valid @RequestBody UpdateThemeRequestDto request) {
        return dashboardService.updateTheme(dashboardId, request.theme(), request.updatedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{dashboardId}/permissions")
    public ResponseEntity<DashboardConfiguration> updatePermissions(
        @PathVariable String dashboardId,
        @Valid @RequestBody UpdatePermissionsRequestDto request) {
        return dashboardService.updatePermissions(dashboardId, request.permissions(), request.updatedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{dashboardId}/activate")
    public ResponseEntity<DashboardConfiguration> activateDashboard(
        @PathVariable String dashboardId,
        @RequestBody Map<String, String> request) {
        return dashboardService.activateDashboard(dashboardId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{dashboardId}/deactivate")
    public ResponseEntity<DashboardConfiguration> deactivateDashboard(
        @PathVariable String dashboardId,
        @RequestBody Map<String, String> request) {
        return dashboardService.deactivateDashboard(dashboardId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{sourceDashboardId}/clone")
    public ResponseEntity<Map<String, String>> cloneDashboard(
        @PathVariable String sourceDashboardId,
        @Valid @RequestBody CloneDashboardRequestDto request) {
        boolean success = dashboardService.cloneDashboard(
            sourceDashboardId,
            request.newDashboardId(),
            request.clonedBy()
        ).join();

        if (success) {
            return ResponseEntity.ok(Map.of("status", "cloned", "newDashboardId", request.newDashboardId()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{dashboardId}")
    public ResponseEntity<Void> deleteDashboard(@PathVariable String dashboardId) {
        dashboardService.deleteDashboard(dashboardId).join();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{dashboardId}/permissions/check")
    public ResponseEntity<Map<String, Boolean>> checkPermission(
        @PathVariable String dashboardId,
        @RequestParam String action,
        @RequestParam String role) {
        boolean hasPermission = dashboardService.hasPermission(dashboardId, action, role).join();
        return ResponseEntity.ok(Map.of("hasPermission", hasPermission));
    }
}
