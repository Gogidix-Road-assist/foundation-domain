package com.gogidix.rapidassist.dashboard.reporting.service.adapters.in.web;

import com.gogidix.rapidassist.dashboard.reporting.service.application.DashboardReportingService;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportDefinition;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportingController {

    private final DashboardReportingService reportingService;

    public ReportingController(DashboardReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "dashboard-reporting-service"
        ));
    }

    // Report Definition Endpoints
    @GetMapping("/definitions")
    public ResponseEntity<List<ReportDefinition>> getAllDefinitions() {
        return ResponseEntity.ok(reportingService.getAllReportDefinitions().join());
    }

    @GetMapping("/definitions/tenant/{tenantId}")
    public ResponseEntity<List<ReportDefinition>> getDefinitionsByTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(reportingService.getReportDefinitionsByTenant(tenantId).join());
    }

    @GetMapping("/definitions/{reportId}")
    public ResponseEntity<ReportDefinition> getDefinition(@PathVariable String reportId) {
        return reportingService.getReportDefinition(reportId).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/definitions")
    public ResponseEntity<ReportDefinition> createDefinition(@RequestBody CreateDefinitionRequest request) {
        ReportDefinition definition = reportingService.createReportDefinition(
            request.tenantId(),
            request.reportId(),
            request.name(),
            request.description(),
            request.reportType(),
            request.createdBy()
        ).join();

        return ResponseEntity.created(URI.create("/api/reports/definitions/" + definition.reportId())).body(definition);
    }

    @PutMapping("/definitions/{reportId}")
    public ResponseEntity<ReportDefinition> updateDefinition(
        @PathVariable String reportId,
        @RequestBody UpdateDefinitionRequest request) {
        return reportingService.updateReportDefinition(reportId, request.name(), request.description(), request.updatedBy()).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/definitions/{reportId}/activate")
    public ResponseEntity<ReportDefinition> activateDefinition(
        @PathVariable String reportId,
        @RequestBody Map<String, String> request) {
        return reportingService.activateReportDefinition(reportId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/definitions/{reportId}/deactivate")
    public ResponseEntity<ReportDefinition> deactivateDefinition(
        @PathVariable String reportId,
        @RequestBody Map<String, String> request) {
        return reportingService.deactivateReportDefinition(reportId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/definitions/{reportId}")
    public ResponseEntity<Void> deleteDefinition(@PathVariable String reportId) {
        reportingService.deleteReportDefinition(reportId).join();
        return ResponseEntity.noContent().build();
    }

    // Report Execution Endpoints
    @PostMapping("/{reportId}/generate")
    public ResponseEntity<ReportExecution> generateReport(
        @PathVariable String reportId,
        @RequestBody GenerateReportRequest request) {
        ReportExecution execution = reportingService.generateReport(
            reportId,
            request.format(),
            request.requestedBy()
        ).join();

        return ResponseEntity.ok(execution);
    }

    @GetMapping("/{reportId}/executions")
    public ResponseEntity<List<ReportExecution>> getExecutions(
        @PathVariable String reportId,
        @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(reportingService.getExecutionsByReport(reportId, limit).join());
    }

    @GetMapping("/executions/{executionId}")
    public ResponseEntity<ReportExecution> getExecution(@PathVariable String executionId) {
        return reportingService.getExecution(executionId).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/executions/{executionId}/cancel")
    public ResponseEntity<ReportExecution> cancelExecution(
        @PathVariable String executionId,
        @RequestBody Map<String, String> request) {
        return reportingService.cancelReportExecution(executionId, request.get("cancelledBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Request records
    record CreateDefinitionRequest(
        String tenantId,
        String reportId,
        String name,
        String description,
        ReportDefinition.ReportType reportType,
        String createdBy
    ) {}

    record UpdateDefinitionRequest(
        String name,
        String description,
        String updatedBy
    ) {}

    record GenerateReportRequest(
        ReportDefinition.ReportFormat format,
        String requestedBy
    ) {}
}
