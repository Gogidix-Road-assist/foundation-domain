package com.gogidix.rapidassist.orchestration.alerting_service.interfaces.rest;

import com.gogidix.rapidassist.orchestration.alerting_service.application.command.AcknowledgeAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.CreateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.EscalateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.ResolveAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertSummaryDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.application.query.GetAlertQuery;
import com.gogidix.rapidassist.orchestration.alerting_service.application.query.ListAlertsQuery;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.input.AlertServicePort;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContextHolder;
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

/**
 * REST controller for Alert operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Tag(name = "Alert Management", description = "APIs for managing roadside assistance alerts")
public class AlertController {

    private final AlertServicePort alertService;

    @PostMapping
    @Operation(summary = "Create a new alert", description = "Creates a new alert for emergency or roadside assistance")
    public ResponseEntity<AlertDTO> createAlert(@Valid @RequestBody CreateAlertCommand command) {
        log.info("POST /api/v1/alerts - Creating alert for request: {}", command.getRequestId());
        AlertDTO alert = alertService.createAlert(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @GetMapping("/{alertId}")
    @Operation(summary = "Get alert by ID", description = "Retrieves a specific alert by its ID")
    public ResponseEntity<AlertDTO> getAlert(
        @Parameter(description = "Alert ID", required = true)
        @PathVariable String alertId
    ) {
        RequestContext context = RequestContextHolder.get();

        GetAlertQuery query = GetAlertQuery.builder()
            .alertId(alertId)
            .tenantId(context.getTenantId())
            .build();

        log.info("GET /api/v1/alerts/{} - Getting alert for tenant: {}", alertId, context.getTenantId());
        AlertDTO alert = alertService.getAlert(query);
        return ResponseEntity.ok(alert);
    }

    @GetMapping
    @Operation(summary = "List alerts", description = "Lists alerts with optional filters")
    public ResponseEntity<List<AlertDTO>> listAlerts(
        @Parameter(description = "Alert type filter") @RequestParam(required = false) String type,
        @Parameter(description = "Alert severity filter") @RequestParam(required = false) String severity,
        @Parameter(description = "Assigned to user") @RequestParam(required = false) String assignedTo,
        @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size
    ) {
        RequestContext context = RequestContextHolder.get();

        ListAlertsQuery query = ListAlertsQuery.builder()
            .tenantId(context.getTenantId())
            .type(type != null ? com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert.AlertType.valueOf(type) : null)
            .severity(severity != null ? com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert.AlertSeverity.valueOf(severity) : null)
            .assignedTo(assignedTo)
            .page(page)
            .size(size)
            .build();

        log.info("GET /api/v1/alerts - Listing alerts for tenant: {}", context.getTenantId());
        List<AlertDTO> alerts = alertService.listAlerts(query);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/request/{requestId}")
    @Operation(summary = "Get alerts by request ID", description = "Retrieves all alerts for a specific request")
    public ResponseEntity<List<AlertDTO>> getAlertsByRequestId(
        @Parameter(description = "Request ID", required = true)
        @PathVariable String requestId
    ) {
        log.info("GET /api/v1/alerts/request/{} - Getting alerts", requestId);
        List<AlertDTO> alerts = alertService.getAlertsByRequestId(requestId);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/tenant/{tenantId}/active")
    @Operation(summary = "Get active alerts for tenant", description = "Retrieves all active alerts for a tenant")
    public ResponseEntity<List<AlertDTO>> getActiveAlerts(
        @Parameter(description = "Tenant ID", required = true)
        @PathVariable String tenantId
    ) {
        log.info("GET /api/v1/alerts/tenant/{}/active - Getting active alerts", tenantId);
        List<AlertDTO> alerts = alertService.getActiveAlertsByTenant(tenantId);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/critical")
    @Operation(summary = "Get critical alerts", description = "Retrieves all critical unattended alerts")
    public ResponseEntity<List<AlertDTO>> getCriticalAlerts() {
        RequestContext context = RequestContextHolder.get();
        log.info("GET /api/v1/alerts/critical - Getting critical alerts for tenant: {}", context.getTenantId());
        List<AlertDTO> alerts = alertService.getCriticalAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/escalation-required")
    @Operation(summary = "Get alerts requiring escalation", description = "Retrieves all alerts that need escalation")
    public ResponseEntity<List<AlertDTO>> getEscalationRequiredAlerts() {
        RequestContext context = RequestContextHolder.get();
        log.info("GET /api/v1/alerts/escalation-required - Getting alerts for tenant: {}", context.getTenantId());
        List<AlertDTO> alerts = alertService.getEscalationRequiredAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/{alertId}/acknowledge")
    @Operation(summary = "Acknowledge an alert", description = "Acknowledges an alert and assigns it to a user")
    public ResponseEntity<AlertDTO> acknowledgeAlert(
        @Parameter(description = "Alert ID", required = true)
        @PathVariable String alertId,
        @Valid @RequestBody AcknowledgeAlertCommand command
    ) {
        log.info("POST /api/v1/alerts/{}/acknowledge - Acknowledging alert", alertId);
        AlertDTO alert = alertService.acknowledgeAlert(alertId, command);
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/{alertId}/escalate")
    @Operation(summary = "Escalate an alert", description = "Escalates an alert to a higher level")
    public ResponseEntity<AlertDTO> escalateAlert(
        @Parameter(description = "Alert ID", required = true)
        @PathVariable String alertId,
        @Valid @RequestBody EscalateAlertCommand command
    ) {
        log.info("POST /api/v1/alerts/{}/escalate - Escalating alert to level: {}", alertId, command.getEscalationLevel());
        AlertDTO alert = alertService.escalateAlert(alertId, command);
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/{alertId}/resolve")
    @Operation(summary = "Resolve an alert", description = "Marks an alert as resolved")
    public ResponseEntity<AlertDTO> resolveAlert(
        @Parameter(description = "Alert ID", required = true)
        @PathVariable String alertId,
        @Valid @RequestBody ResolveAlertCommand command
    ) {
        log.info("POST /api/v1/alerts/{}/resolve - Resolving alert", alertId);
        AlertDTO alert = alertService.resolveAlert(alertId, command);
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/{alertId}/close")
    @Operation(summary = "Close an alert", description = "Closes a resolved alert")
    public ResponseEntity<AlertDTO> closeAlert(
        @Parameter(description = "Alert ID", required = true)
        @PathVariable String alertId
    ) {
        log.info("POST /api/v1/alerts/{}/close - Closing alert", alertId);
        AlertDTO alert = alertService.closeAlert(alertId);
        return ResponseEntity.ok(alert);
    }

    @DeleteMapping("/{alertId}")
    @Operation(summary = "Delete an alert", description = "Deletes an alert (soft delete)")
    public ResponseEntity<Void> deleteAlert(
        @Parameter(description = "Alert ID", required = true)
        @PathVariable String alertId
    ) {
        log.info("DELETE /api/v1/alerts/{} - Deleting alert", alertId);
        alertService.deleteAlert(alertId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Checks if the alerting service is healthy")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Alerting service is healthy");
    }
}
