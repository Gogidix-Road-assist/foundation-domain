package com.gogidix.rapidassist.ai.dataquality.infrastructure.rest.controller;

import com.gogidix.rapidassist.ai.dataquality.application.command.*;
import com.gogidix.rapidassist.ai.dataquality.application.dto.*;
import com.gogidix.rapidassist.ai.dataquality.application.query.*;
import com.gogidix.rapidassist.ai.dataquality.application.service.DataQualityApplicationService;
import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import com.gogidix.rapidassist.shared.request.context.library.domain.TenantContext;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.rest.request.*;
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
 * REST Controller for Data Quality operations
 * API endpoint: /api/v1/data-quality
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Data Quality", description = "Data Quality Management APIs")
@RequestMapping("/api/v1/data-quality")
public class DataQualityController {

    private final DataQualityApplicationService applicationService;

    // ==================== Check Operations ====================

    @PostMapping("/check")
    @Operation(summary = "Execute data quality check", description = "Runs a data quality check for a specific rule")
    public ResponseEntity<DataQualityCheckDto> executeCheck(
            @Valid @RequestBody ExecuteCheckRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is set by interceptors/filters
        var command = ExecuteDataQualityCheckCommand.builder()
                .tenantId(tenantId)
                .ruleId(request.getRuleId())
                .checkName(request.getCheckName())
                .entityType(request.getEntityType())
                .datasetIdentifier(request.getDatasetIdentifier())
                .checkParameters(request.getCheckParameters())
                .executedBy(request.getExecutedBy())
                .build();

        DataQualityCheckDto result = applicationService.executeCheck(command);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/checks/{id}")
    @Operation(summary = "Get check result", description = "Retrieves a data quality check by ID")
    public ResponseEntity<DataQualityCheckDto> getCheck(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context handled by filters/interceptors

        var query = GetDataQualityCheckQuery.builder()
                .tenantId(tenantId)
                .checkId(id)
                .build();

        DataQualityCheckDto result = applicationService.getCheck(query);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/checks")
    @Operation(summary = "List all checks", description = "Lists all data quality checks with optional filtering")
    public ResponseEntity<List<DataQualityCheckDto>> listChecks(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by rule ID") @RequestParam(required = false) UUID ruleId) {

        // Tenant context handled by filters/interceptors

        if (ruleId != null) {
            return ResponseEntity.ok(applicationService.listChecks(tenantId, ruleId));
        }

        return ResponseEntity.ok(List.of());
    }

    // ==================== Rule Operations ====================

    @PostMapping("/rules")
    @Operation(summary = "Create quality rule", description = "Creates a new data quality rule")
    public ResponseEntity<DataQualityRuleDto> createRule(
            @Valid @RequestBody CreateRuleRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is set by interceptors/filters
        var command = CreateDataQualityRuleCommand.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .ruleType(request.getRuleType())
                .entityType(request.getEntityType())
                .attributeName(request.getAttributeName())
                .operator(request.getOperator())
                .thresholdValue(request.getThresholdValue())
                .parameters(request.getParameters())
                .severity(request.getSeverity())
                .active(request.getActive() != null ? request.getActive() : true)
                .createdBy(request.getCreatedBy())
                .build();

        DataQualityRuleDto result = applicationService.createRule(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/rules/{id}")
    @Operation(summary = "Update quality rule", description = "Updates an existing data quality rule")
    public ResponseEntity<DataQualityRuleDto> updateRule(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRuleRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is set by interceptors/filters
        var command = UpdateDataQualityRuleCommand.builder()
                .tenantId(tenantId)
                .ruleId(id)
                .name(request.getName())
                .description(request.getDescription())
                .thresholdValue(request.getThresholdValue())
                .parameters(request.getParameters())
                .updatedBy(request.getUpdatedBy())
                .build();

        DataQualityRuleDto result = applicationService.updateRule(command);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/rules/{id}")
    @Operation(summary = "Delete quality rule", description = "Deletes a data quality rule")
    public ResponseEntity<Void> deleteRule(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context handled by filters/interceptors
        applicationService.deleteRule(tenantId, id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rules")
    @Operation(summary = "List all rules", description = "Lists all data quality rules with optional filtering")
    public ResponseEntity<List<DataQualityRuleDto>> listRules(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by rule type") @RequestParam(required = false) DataQualityRule.RuleType ruleType,
            @Parameter(description = "Filter by entity type") @RequestParam(required = false) String entityType,
            @Parameter(description = "Filter by severity") @RequestParam(required = false) DataQualityRule.RuleSeverity severity,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean active) {

        // Tenant context handled by filters/interceptors

        var query = ListDataQualityRulesQuery.builder()
                .tenantId(tenantId)
                .ruleType(ruleType)
                .entityType(entityType)
                .severity(severity)
                .active(active)
                .build();

        List<DataQualityRuleDto> results = applicationService.listRules(query);
        return ResponseEntity.ok(results);
    }

    // ==================== Report Operations ====================

    @PostMapping("/reports/generate")
    @Operation(summary = "Generate quality report", description = "Generates a data quality report for a specified period")
    public ResponseEntity<DataQualityReportDto> generateReport(
            @Valid @RequestBody GenerateReportRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is set by interceptors/filters
        var command = GenerateDataQualityReportCommand.builder()
                .tenantId(tenantId)
                .reportName(request.getReportName())
                .reportType(request.getReportType())
                .reportPeriodStart(request.getReportPeriodStart() != null ? request.getReportPeriodStart() : LocalDateTime.now().minusDays(30))
                .reportPeriodEnd(request.getReportPeriodEnd() != null ? request.getReportPeriodEnd() : LocalDateTime.now())
                .filters(request.getFilters())
                .generatedBy(request.getGeneratedBy())
                .build();

        DataQualityReportDto result = applicationService.generateReport(command);
        return ResponseEntity.ok(result);
    }

    // ==================== Issue Operations ====================

    @GetMapping("/issues")
    @Operation(summary = "List detected issues", description = "Lists all detected data quality issues with optional filtering")
    public ResponseEntity<List<DataQualityIssueDto>> listIssues(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityIssue.IssueStatus status,
            @Parameter(description = "Filter by severity") @RequestParam(required = false) com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityIssue.IssueSeverity severity,
            @Parameter(description = "Filter by entity type") @RequestParam(required = false) String entityType,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size) {

        // Tenant context handled by filters/interceptors

        var query = ListDataQualityIssuesQuery.builder()
                .tenantId(tenantId)
                .status(status)
                .severity(severity)
                .entityType(entityType)
                .page(page)
                .size(size)
                .build();

        List<DataQualityIssueDto> results = applicationService.listIssues(query);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/issues/{id}/resolve")
    @Operation(summary = "Resolve issue", description = "Marks a data quality issue as resolved")
    public ResponseEntity<DataQualityIssueDto> resolveIssue(
            @PathVariable UUID id,
            @Valid @RequestBody ResolveIssueRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is set by interceptors/filters
        var command = ResolveDataQualityIssueCommand.builder()
                .tenantId(tenantId)
                .issueId(id)
                .resolvedBy(request.getResolvedBy())
                .resolutionNotes(request.getResolutionNotes())
                .build();

        DataQualityIssueDto result = applicationService.resolveIssue(command);
        return ResponseEntity.ok(result);
    }

    // ==================== Metrics Operations ====================

    @GetMapping("/metrics")
    @Operation(summary = "Get quality metrics", description = "Retrieves data quality metrics with optional filtering")
    public ResponseEntity<List<DataQualityMetricDto>> getMetrics(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by metric name") @RequestParam(required = false) String metricName,
            @Parameter(description = "Filter by entity type") @RequestParam(required = false) String entityType,
            @Parameter(description = "Start date") @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) LocalDateTime endDate,
            @Parameter(description = "Limit results") @RequestParam(defaultValue = "50") Integer limit) {

        // Tenant context handled by filters/interceptors

        var query = GetDataQualityMetricsQuery.builder()
                .tenantId(tenantId)
                .metricName(metricName)
                .entityType(entityType)
                .startDate(startDate)
                .endDate(endDate)
                .limit(limit)
                .build();

        List<DataQualityMetricDto> results = applicationService.getMetrics(query);
        return ResponseEntity.ok(results);
    }
}
