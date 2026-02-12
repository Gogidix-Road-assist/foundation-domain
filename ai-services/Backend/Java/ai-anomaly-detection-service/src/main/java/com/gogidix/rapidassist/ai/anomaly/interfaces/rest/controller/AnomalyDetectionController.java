package com.gogidix.rapidassist.ai.anomaly.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.anomaly.application.command.AcknowledgeAlertCommand;
import com.gogidix.rapidassist.ai.anomaly.application.command.CreateDetectionRuleCommand;
import com.gogidix.rapidassist.ai.anomaly.application.command.DetectAnomaliesCommand;
import com.gogidix.rapidassist.ai.anomaly.application.command.UpdateDetectionRuleCommand;
import com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyAlertDto;
import com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyDetectionDto;
import com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyPatternDto;
import com.gogidix.rapidassist.ai.anomaly.application.dto.DetectionRuleDto;
import com.gogidix.rapidassist.ai.anomaly.application.port.in.AnomalyDetectionUseCase;
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
 * REST Controller for Anomaly Detection API.
 * Exposes REST endpoints for anomaly detection operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/anomaly-detection")
@RequiredArgsConstructor
@Tag(name = "Anomaly Detection", description = "APIs for anomaly detection and alert management")
public class AnomalyDetectionController {

    private final AnomalyDetectionUseCase anomalyDetectionUseCase;

    @PostMapping("/detect")
    @Operation(summary = "Detect anomalies in data", description = "Analyzes data for anomalies using active detection rules")
    public ResponseEntity<List<AnomalyDetectionDto>> detectAnomalies(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody DetectAnomaliesCommand command) {

        command.setTenantId(tenantId);
        log.info("Detecting anomalies for tenant: {}, data source: {}", tenantId, command.getDataSource());

        List<AnomalyDetectionDto> detections = anomalyDetectionUseCase.detectAnomalies(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(detections);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detection by ID", description = "Retrieve a specific anomaly detection by its ID")
    public ResponseEntity<AnomalyDetectionDto> getDetectionById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Detection ID") @PathVariable UUID id) {

        AnomalyDetectionDto detection = anomalyDetectionUseCase.getDetectionById(tenantId, id);
        return ResponseEntity.ok(detection);
    }

    @GetMapping
    @Operation(summary = "List all detections", description = "Retrieve all anomaly detections with pagination")
    public ResponseEntity<List<AnomalyDetectionDto>> listDetections(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        List<AnomalyDetectionDto> detections = anomalyDetectionUseCase.listDetections(tenantId, page, size);
        return ResponseEntity.ok(detections);
    }

    @PostMapping("/rules")
    @Operation(summary = "Create detection rule", description = "Create a new anomaly detection rule")
    public ResponseEntity<DetectionRuleDto> createRule(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreateDetectionRuleCommand command) {

        command.setTenantId(tenantId);
        log.info("Creating detection rule for tenant: {}, name: {}", tenantId, command.getName());

        DetectionRuleDto rule = anomalyDetectionUseCase.createRule(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(rule);
    }

    @PutMapping("/rules/{id}")
    @Operation(summary = "Update detection rule", description = "Update an existing detection rule")
    public ResponseEntity<DetectionRuleDto> updateRule(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Rule ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateDetectionRuleCommand command) {

        command.setRuleId(id);
        command.setTenantId(tenantId);
        log.info("Updating detection rule for tenant: {}, id: {}", tenantId, id);

        DetectionRuleDto rule = anomalyDetectionUseCase.updateRule(command);
        return ResponseEntity.ok(rule);
    }

    @DeleteMapping("/rules/{id}")
    @Operation(summary = "Delete detection rule", description = "Delete a detection rule by ID")
    public ResponseEntity<Void> deleteRule(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Rule ID") @PathVariable UUID id) {

        log.info("Deleting detection rule for tenant: {}, id: {}", tenantId, id);
        anomalyDetectionUseCase.deleteRule(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rules")
    @Operation(summary = "List all rules", description = "Retrieve all detection rules for the tenant")
    public ResponseEntity<List<DetectionRuleDto>> listRules(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        List<DetectionRuleDto> rules = anomalyDetectionUseCase.listRules(tenantId);
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/rules/{id}")
    @Operation(summary = "Get rule by ID", description = "Retrieve a specific detection rule by its ID")
    public ResponseEntity<DetectionRuleDto> getRuleById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Rule ID") @PathVariable UUID id) {

        DetectionRuleDto rule = anomalyDetectionUseCase.getRuleById(tenantId, id);
        return ResponseEntity.ok(rule);
    }

    @GetMapping("/patterns")
    @Operation(summary = "List all patterns", description = "Retrieve all anomaly detection patterns")
    public ResponseEntity<List<AnomalyPatternDto>> listPatterns(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        List<AnomalyPatternDto> patterns = anomalyDetectionUseCase.listPatterns(tenantId);
        return ResponseEntity.ok(patterns);
    }

    @PostMapping("/alerts/{id}/acknowledge")
    @Operation(summary = "Acknowledge alert", description = "Acknowledge an anomaly alert")
    public ResponseEntity<AnomalyAlertDto> acknowledgeAlert(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Alert ID") @PathVariable UUID id,
            @Valid @RequestBody AcknowledgeAlertCommand command) {

        command.setAlertId(id);
        command.setTenantId(tenantId);
        log.info("Acknowledging alert for tenant: {}, id: {}", tenantId, id);

        AnomalyAlertDto alert = anomalyDetectionUseCase.acknowledgeAlert(command);
        return ResponseEntity.ok(alert);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    record ErrorResponse(String message, int status) {}
}
