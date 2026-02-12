package com.gogidix.rapidassist.ai.riskassessment.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.riskassessment.application.command.AddRiskFactorCommand;
import com.gogidix.rapidassist.ai.riskassessment.application.command.CreateRiskAssessmentCommand;
import com.gogidix.rapidassist.ai.riskassessment.application.command.UpdateAssessmentStatusCommand;
import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskAssessmentDto;
import com.gogidix.rapidassist.ai.riskassessment.application.service.RiskAssessmentApplicationService;
import com.gogidix.rapidassist.ai.riskassessment.domain.tenant.TenantContext;
import com.gogidix.rapidassist.ai.riskassessment.interfaces.rest.request.AddFactorRequest;
import com.gogidix.rapidassist.ai.riskassessment.interfaces.rest.request.CreateAssessmentRequest;
import com.gogidix.rapidassist.ai.riskassessment.interfaces.rest.request.UpdateStatusRequest;
import com.gogidix.rapidassist.ai.riskassessment.interfaces.rest.response.AssessmentResponse;
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
 * REST Controller for Risk Assessment operations.
 * API endpoint: /api/v1/risk-assessments
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/risk-assessments")
@RequiredArgsConstructor
@Tag(name = "Risk Assessment", description = "Risk Assessment Management APIs")
public class RiskAssessmentRestController {

    private final RiskAssessmentApplicationService applicationService;

    /**
     * Create a new risk assessment.
     * POST /api/v1/risk-assessments
     */
    @PostMapping
    @Operation(summary = "Create risk assessment", description = "Creates a new risk assessment for a subject")
    public ResponseEntity<AssessmentResponse> createAssessment(
            @Valid @RequestBody CreateAssessmentRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {

        TenantContext.setTenantId(tenantId);

        var command = CreateRiskAssessmentCommand.builder()
                .tenantId(tenantId)
                .subjectId(request.getSubjectId())
                .subjectType(request.getSubjectType())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .createdBy(userId)
                .metadata(request.getMetadata())
                .build();

        RiskAssessmentDto dto = applicationService.createAssessment(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(dto));
    }

    /**
     * Get a risk assessment by ID.
     * GET /api/v1/risk-assessments/{assessmentId}
     */
    @GetMapping("/{assessmentId}")
    @Operation(summary = "Get risk assessment", description = "Retrieves a risk assessment by ID")
    public ResponseEntity<AssessmentResponse> getAssessment(
            @PathVariable UUID assessmentId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Include risk factors") @RequestParam(defaultValue = "true") Boolean includeFactors,
            @Parameter(description = "Include alerts") @RequestParam(defaultValue = "true") Boolean includeAlerts) {

        TenantContext.setTenantId(tenantId);

        RiskAssessmentDto dto = applicationService.getAssessment(assessmentId, tenantId);

        return ResponseEntity.ok(toResponse(dto));
    }

    /**
     * Get assessments by subject.
     * GET /api/v1/risk-assessments/subject/{subjectId}
     */
    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Get subject assessments", description = "Retrieves all risk assessments for a subject")
    public ResponseEntity<List<AssessmentResponse>> getSubjectAssessments(
            @PathVariable String subjectId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<RiskAssessmentDto> dtos = applicationService.getAssessmentsBySubject(subjectId, tenantId);

        return ResponseEntity.ok(dtos.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Get active assessments for tenant.
     * GET /api/v1/risk-assessments/active
     */
    @GetMapping("/active")
    @Operation(summary = "Get active assessments", description = "Retrieves all active risk assessments for a tenant")
    public ResponseEntity<List<AssessmentResponse>> getActiveAssessments(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<RiskAssessmentDto> dtos = applicationService.getActiveAssessments(tenantId);

        return ResponseEntity.ok(dtos.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Get critical risk assessments for tenant.
     * GET /api/v1/risk-assessments/critical
     */
    @GetMapping("/critical")
    @Operation(summary = "Get critical assessments", description = "Retrieves all critical risk assessments for a tenant")
    public ResponseEntity<List<AssessmentResponse>> getCriticalAssessments(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<RiskAssessmentDto> dtos = applicationService.getCriticalRiskAssessments(tenantId);

        return ResponseEntity.ok(dtos.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Add risk factor to assessment.
     * POST /api/v1/risk-assessments/{assessmentId}/factors
     */
    @PostMapping("/{assessmentId}/factors")
    @Operation(summary = "Add risk factor", description = "Adds a risk factor to an assessment")
    public ResponseEntity<AssessmentResponse> addRiskFactor(
            @PathVariable UUID assessmentId,
            @Valid @RequestBody AddFactorRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {

        TenantContext.setTenantId(tenantId);

        var command = AddRiskFactorCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .weight(request.getWeight())
                .score(request.getScore())
                .impact(request.getImpact())
                .likelihood(request.getLikelihood())
                .metadata(request.getMetadata())
                .createdBy(userId)
                .build();

        RiskAssessmentDto dto = applicationService.addRiskFactor(command);

        return ResponseEntity.ok(toResponse(dto));
    }

    /**
     * Update assessment status.
     * PATCH /api/v1/risk-assessments/{assessmentId}/status
     */
    @PatchMapping("/{assessmentId}/status")
    @Operation(summary = "Update status", description = "Updates the status of a risk assessment")
    public ResponseEntity<AssessmentResponse> updateStatus(
            @PathVariable UUID assessmentId,
            @Valid @RequestBody UpdateStatusRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {

        TenantContext.setTenantId(tenantId);

        var command = UpdateAssessmentStatusCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .status(request.getStatus())
                .reason(request.getReason())
                .updatedBy(userId)
                .build();

        RiskAssessmentDto dto = applicationService.updateStatus(command);

        return ResponseEntity.ok(toResponse(dto));
    }

    /**
     * Complete assessment and generate alerts.
     * POST /api/v1/risk-assessments/{assessmentId}/complete
     */
    @PostMapping("/{assessmentId}/complete")
    @Operation(summary = "Complete assessment", description = "Completes the assessment and generates alerts")
    public ResponseEntity<AssessmentResponse> completeAssessment(
            @PathVariable UUID assessmentId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        RiskAssessmentDto dto = applicationService.completeAssessmentAndGenerateAlerts(assessmentId, tenantId);

        return ResponseEntity.ok(toResponse(dto));
    }

    /**
     * Delete a risk assessment.
     * DELETE /api/v1/risk-assessments/{assessmentId}
     */
    @DeleteMapping("/{assessmentId}")
    @Operation(summary = "Delete assessment", description = "Deletes a risk assessment")
    public ResponseEntity<Void> deleteAssessment(
            @PathVariable UUID assessmentId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.deleteAssessment(assessmentId, tenantId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Convert DTO to Response
     */
    private AssessmentResponse toResponse(RiskAssessmentDto dto) {
        return AssessmentResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .assessmentCode(dto.getAssessmentCode())
                .subjectId(dto.getSubjectId())
                .subjectType(dto.getSubjectType())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .category(dto.getCategory())
                .overallRiskScore(dto.getOverallRiskScore())
                .riskLevel(dto.getRiskLevel())
                .assessedBy(dto.getAssessedBy())
                .reviewedBy(dto.getReviewedBy())
                .reviewedAt(dto.getReviewedAt())
                .approvedBy(dto.getApprovedBy())
                .approvedAt(dto.getApprovedAt())
                .rejectionReason(dto.getRejectionReason())
                .metadata(dto.getMetadata())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .riskFactors(dto.getRiskFactors())
                .alerts(dto.getAlerts() != null ? dto.getAlerts().stream()
                        .map(alert -> com.gogidix.rapidassist.ai.riskassessment.interfaces.rest.response.AlertSummaryResponse.builder()
                                .id(alert.getId())
                                .title(alert.getTitle())
                                .priority(alert.getPriority())
                                .status(alert.getStatus())
                                .riskScore(alert.getRiskScore())
                                .riskLevel(alert.getRiskLevel())
                                .category(alert.getCategory())
                                .createdAt(alert.getCreatedAt())
                                .build())
                        .toList() : null)
                .mitigationRecommendations(dto.getMitigationRecommendations())
                .riskFactorCount(dto.getRiskFactorCount())
                .highRiskFactorCount(dto.getHighRiskFactorCount())
                .criticalFactorCount(dto.getCriticalFactorCount())
                .activeAlertCount(dto.getActiveAlertCount())
                .unresolvedAlertCount(dto.getUnresolvedAlertCount())
                .durationDays(dto.getDurationDays())
                .hasCriticalRisks(dto.isHasCriticalRisks())
                .allAlertsResolved(dto.isAllAlertsResolved())
                .build();
    }
}
