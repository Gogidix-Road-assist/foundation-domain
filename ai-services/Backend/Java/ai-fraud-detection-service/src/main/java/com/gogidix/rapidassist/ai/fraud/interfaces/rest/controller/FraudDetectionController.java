package com.gogidix.rapidassist.ai.fraud.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.fraud.application.dto.FraudDetectionDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudAlertDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudCaseDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudPatternDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudRuleDto;
import com.gogidix.rapidassist.ai.fraud.application.service.FraudDetectionService;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
import com.gogidix.rapidassist.ai.fraud.domain.tenant.TenantContext;
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
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Fraud Detection operations
 * API endpoint: /api/v1/fraud-detection
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/fraud-detection")
@RequiredArgsConstructor
@Tag(name = "Fraud Detection", description = "Fraud Detection APIs")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    // ==================== Fraud Detection Endpoints ====================

    @PostMapping("/detect")
    @Operation(summary = "Detect fraud", description = "Performs fraud detection on an entity")
    public ResponseEntity<FraudDetectionDto> detectFraud(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody DetectFraudRequest request) {

        TenantContext.setTenantId(tenantId);

        FraudDetectionDto result = fraudDetectionService.detectFraud(
                tenantId,
                request.getEntityType(),
                request.getEntityId(),
                request.getRiskLevel(),
                request.getRiskScore(),
                request.getDetectionMethod(),
                request.getDetails()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detection by ID", description = "Retrieves a fraud detection by ID")
    public ResponseEntity<FraudDetectionDto> getDetectionById(
            @PathVariable("id") UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        FraudDetectionDto detection = fraudDetectionService.getDetectionById(tenantId, id);
        return ResponseEntity.ok(detection);
    }

    @GetMapping
    @Operation(summary = "List all detections", description = "Lists all fraud detections with optional filtering")
    public ResponseEntity<List<FraudDetectionDto>> getAllDetections(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by status") @RequestParam(value = "status", required = false) String status) {

        TenantContext.setTenantId(tenantId);

        List<FraudDetectionDto> detections = status != null
                ? fraudDetectionService.getDetectionsByStatus(tenantId, status)
                : fraudDetectionService.getAllDetections(tenantId);

        return ResponseEntity.ok(detections);
    }

    // ==================== Fraud Alert Endpoints ====================

    @GetMapping("/alerts")
    @Operation(summary = "List all alerts", description = "Lists all fraud alerts")
    public ResponseEntity<List<FraudAlertDto>> getAllAlerts(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by status") @RequestParam(value = "status", required = false) String status) {

        TenantContext.setTenantId(tenantId);

        List<FraudAlertDto> alerts = status != null
                ? fraudDetectionService.getAlertsByStatus(tenantId, status)
                : fraudDetectionService.getAllAlerts(tenantId);

        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/alerts/{id}/acknowledge")
    @Operation(summary = "Acknowledge alert", description = "Acknowledges a fraud alert")
    public ResponseEntity<FraudAlertDto> acknowledgeAlert(
            @PathVariable("id") UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody AcknowledgeAlertRequest request) {

        TenantContext.setTenantId(tenantId);
        FraudAlertDto alert = fraudDetectionService.acknowledgeAlert(
                tenantId, id, request.getAcknowledgedBy(), request.getNotes()
        );
        return ResponseEntity.ok(alert);
    }

    // ==================== Fraud Case Endpoints ====================

    @PostMapping("/cases")
    @Operation(summary = "Create fraud case", description = "Creates a new fraud investigation case")
    public ResponseEntity<FraudCaseDto> createCase(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreateCaseRequest request) {

        TenantContext.setTenantId(tenantId);

        FraudCaseDto caseDto = fraudDetectionService.createCase(
                tenantId,
                request.getCaseType(),
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getAssignedTo()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(caseDto);
    }

    @PutMapping("/cases/{id}")
    @Operation(summary = "Update fraud case", description = "Updates an existing fraud case")
    public ResponseEntity<FraudCaseDto> updateCase(
            @PathVariable("id") UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody UpdateCaseRequest request) {

        TenantContext.setTenantId(tenantId);

        FraudCaseDto caseDto = fraudDetectionService.updateCase(
                tenantId, id, request.getAssignedTo(), request.getFinding(), request.getDecision()
        );

        return ResponseEntity.ok(caseDto);
    }

    @GetMapping("/cases")
    @Operation(summary = "List all cases", description = "Lists all fraud cases")
    public ResponseEntity<List<FraudCaseDto>> getAllCases(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        List<FraudCaseDto> cases = fraudDetectionService.getAllCases(tenantId);
        return ResponseEntity.ok(cases);
    }

    // ==================== Fraud Rule Endpoints ====================

    @PostMapping("/rules")
    @Operation(summary = "Create detection rule", description = "Creates a new fraud detection rule")
    public ResponseEntity<FraudRuleDto> createRule(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreateRuleRequest request) {

        TenantContext.setTenantId(tenantId);

        FraudRuleDto rule = fraudDetectionService.createRule(
                tenantId,
                request.getRuleName(),
                request.getRuleCode(),
                request.getDescription(),
                request.getRuleType(),
                request.getConditions(),
                request.getAction(),
                request.getPriority()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(rule);
    }

    @GetMapping("/rules")
    @Operation(summary = "List all rules", description = "Lists all fraud detection rules")
    public ResponseEntity<List<FraudRuleDto>> getAllRules(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        List<FraudRuleDto> rules = fraudDetectionService.getAllRules(tenantId);
        return ResponseEntity.ok(rules);
    }

    // ==================== Fraud Pattern Endpoints ====================

    @PostMapping("/patterns")
    @Operation(summary = "Create fraud pattern", description = "Creates a new fraud pattern")
    public ResponseEntity<FraudPatternDto> createPattern(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreatePatternRequest request) {

        TenantContext.setTenantId(tenantId);

        FraudPatternDto pattern = fraudDetectionService.createPattern(
                tenantId,
                request.getPatternName(),
                request.getPatternType(),
                request.getDescription(),
                request.getDefinition()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(pattern);
    }

    @GetMapping("/patterns")
    @Operation(summary = "List all patterns", description = "Lists all fraud patterns")
    public ResponseEntity<List<FraudPatternDto>> getAllPatterns(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);
        List<FraudPatternDto> patterns = fraudDetectionService.getAllPatterns(tenantId);
        return ResponseEntity.ok(patterns);
    }

    // ==================== Exception Handler ====================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    // ==================== Request DTOs ====================

    public static class DetectFraudRequest {
        private String entityType;
        private String entityId;
        private FraudRiskLevel riskLevel;
        private Double riskScore;
        private String detectionMethod;
        private Map<String, Object> details;

        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
        public String getEntityId() { return entityId; }
        public void setEntityId(String entityId) { this.entityId = entityId; }
        public FraudRiskLevel getRiskLevel() { return riskLevel; }
        public void setRiskLevel(FraudRiskLevel riskLevel) { this.riskLevel = riskLevel; }
        public Double getRiskScore() { return riskScore; }
        public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
        public String getDetectionMethod() { return detectionMethod; }
        public void setDetectionMethod(String detectionMethod) { this.detectionMethod = detectionMethod; }
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
    }

    public static class AcknowledgeAlertRequest {
        private String acknowledgedBy;
        private String notes;

        public String getAcknowledgedBy() { return acknowledgedBy; }
        public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class CreateCaseRequest {
        private String caseType;
        private String title;
        private String description;
        private String priority;
        private String assignedTo;

        public String getCaseType() { return caseType; }
        public void setCaseType(String caseType) { this.caseType = caseType; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getAssignedTo() { return assignedTo; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    }

    public static class UpdateCaseRequest {
        private String assignedTo;
        private String finding;
        private String decision;

        public String getAssignedTo() { return assignedTo; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
        public String getFinding() { return finding; }
        public void setFinding(String finding) { this.finding = finding; }
        public String getDecision() { return decision; }
        public void setDecision(String decision) { this.decision = decision; }
    }

    public static class CreateRuleRequest {
        private String ruleName;
        private String ruleCode;
        private String description;
        private String ruleType;
        private Map<String, Object> conditions;
        private String action;
        private Integer priority;

        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        public String getRuleCode() { return ruleCode; }
        public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getRuleType() { return ruleType; }
        public void setRuleType(String ruleType) { this.ruleType = ruleType; }
        public Map<String, Object> getConditions() { return conditions; }
        public void setConditions(Map<String, Object> conditions) { this.conditions = conditions; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }
    }

    public static class CreatePatternRequest {
        private String patternName;
        private String patternType;
        private String description;
        private Map<String, Object> definition;

        public String getPatternName() { return patternName; }
        public void setPatternName(String patternName) { this.patternName = patternName; }
        public String getPatternType() { return patternType; }
        public void setPatternType(String patternType) { this.patternType = patternType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getDefinition() { return definition; }
        public void setDefinition(Map<String, Object> definition) { this.definition = definition; }
    }
}
