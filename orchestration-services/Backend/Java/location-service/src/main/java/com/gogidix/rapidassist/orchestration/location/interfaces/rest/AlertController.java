package com.gogidix.rapidassist.orchestration.location.interfaces.rest;

import com.gogidix.rapidassist.orchestration.location.application.service.AlertService;
import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for Alert operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AlertController.class);
    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<LocationAlert> createAlert(
            @RequestParam String tenantId,
            @RequestBody LocationAlert alert
    ) {
        log.info("Creating alert for tenant: {}", tenantId);
        LocationAlert created = alertService.createAlert(tenantId, alert);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{alertId}")
    public ResponseEntity<LocationAlert> getAlert(
            @PathVariable String alertId,
            @RequestParam String tenantId
    ) {
        LocationAlert alert = alertService.getAlert(tenantId, alertId);
        return ResponseEntity.ok(alert);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LocationAlert>> getPendingAlerts(@RequestParam String tenantId) {
        List<LocationAlert> alerts = alertService.getPendingAlerts(tenantId);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<LocationAlert>> getAlertsForEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @RequestParam String tenantId
    ) {
        List<LocationAlert> alerts = alertService.getAlertsForEntity(
                tenantId,
                com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType.valueOf(entityType.toUpperCase()),
                entityId
        );
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/timerange")
    public ResponseEntity<List<LocationAlert>> getAlertsWithinTimeRange(
            @RequestParam String tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        List<LocationAlert> alerts = alertService.getAlertsWithinTimeRange(tenantId, startTime, endTime);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<LocationAlert>> getRecentAlerts(
            @RequestParam String tenantId,
            @RequestParam(defaultValue = "50") Integer limit
    ) {
        List<LocationAlert> alerts = alertService.getRecentAlerts(tenantId, limit);
        return ResponseEntity.ok(alerts);
    }

    @PatchMapping("/{alertId}/acknowledge")
    public ResponseEntity<LocationAlert> acknowledgeAlert(
            @PathVariable String alertId,
            @RequestParam String tenantId,
            @RequestParam String acknowledgedBy
    ) {
        log.info("Acknowledging alert: {} for tenant: {}", alertId, tenantId);
        LocationAlert alert = alertService.acknowledgeAlert(tenantId, alertId, acknowledgedBy);
        return ResponseEntity.ok(alert);
    }

    @PatchMapping("/{alertId}/resolve")
    public ResponseEntity<LocationAlert> resolveAlert(
            @PathVariable String alertId,
            @RequestParam String tenantId
    ) {
        log.info("Resolving alert: {} for tenant: {}", alertId, tenantId);
        LocationAlert alert = alertService.resolveAlert(tenantId, alertId);
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/send-pending")
    public ResponseEntity<Void> sendPendingAlerts(@RequestParam String tenantId) {
        log.info("Sending pending alerts for tenant: {}", tenantId);
        alertService.sendPendingAlerts(tenantId);
        return ResponseEntity.accepted().build();
    }
}
