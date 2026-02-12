package com.gogidix.rapidassist.orchestration.location.interfaces.rest;

import com.gogidix.rapidassist.orchestration.location.application.service.GeofenceService;
import com.gogidix.rapidassist.orchestration.location.domain.model.Geofence;
import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Geofence operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/geofences")
@RequiredArgsConstructor
public class GeofenceController {

    private final GeofenceService geofenceService;

    @PostMapping
    public ResponseEntity<Geofence> createGeofence(
            @RequestParam String tenantId,
            @Valid @RequestBody Geofence geofence
    ) {
        log.info("Creating geofence: {} for tenant: {}", geofence.getName(), tenantId);
        Geofence created = geofenceService.createGeofence(tenantId, geofence);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{geofenceId}")
    public ResponseEntity<Geofence> updateGeofence(
            @PathVariable String geofenceId,
            @RequestParam String tenantId,
            @Valid @RequestBody Geofence geofence
    ) {
        log.info("Updating geofence: {} for tenant: {}", geofenceId, tenantId);
        Geofence updated = geofenceService.updateGeofence(tenantId, geofenceId, geofence);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{geofenceId}")
    public ResponseEntity<Geofence> getGeofence(
            @PathVariable String geofenceId,
            @RequestParam String tenantId
    ) {
        Geofence geofence = geofenceService.getGeofence(tenantId, geofenceId);
        return ResponseEntity.ok(geofence);
    }

    @GetMapping
    public ResponseEntity<List<Geofence>> getGeofences(@RequestParam String tenantId) {
        List<Geofence> geofences = geofenceService.getGeofences(tenantId);
        return ResponseEntity.ok(geofences);
    }

    @GetMapping("/active/{entityType}")
    public ResponseEntity<List<Geofence>> getActiveGeofences(
            @PathVariable String entityType,
            @RequestParam String tenantId
    ) {
        List<Geofence> geofences = geofenceService.getActiveGeofencesForEntityType(
                tenantId,
                com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType.valueOf(entityType.toUpperCase())
        );
        return ResponseEntity.ok(geofences);
    }

    @GetMapping("/containing")
    public ResponseEntity<List<Geofence>> findGeofencesContainingPoint(
            @RequestParam String tenantId,
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        List<Geofence> geofences = geofenceService.findGeofencesContainingPoint(
                tenantId, latitude, longitude
        );
        return ResponseEntity.ok(geofences);
    }

    @DeleteMapping("/{geofenceId}")
    public ResponseEntity<Void> deleteGeofence(
            @PathVariable String geofenceId,
            @RequestParam String tenantId
    ) {
        geofenceService.deleteGeofence(tenantId, geofenceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{geofenceId}/alerts")
    public ResponseEntity<List<LocationAlert>> getGeofenceAlerts(
            @PathVariable String geofenceId,
            @RequestParam String tenantId
    ) {
        List<LocationAlert> alerts = geofenceService.getGeofenceAlerts(tenantId, geofenceId);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/check/{entityType}/{entityId}")
    public ResponseEntity<Void> checkGeofences(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @RequestParam String tenantId,
            @Valid @RequestBody Location location
    ) {
        geofenceService.processGeofenceBreaches(
                tenantId,
                entityId,
                com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType.valueOf(entityType.toUpperCase()),
                location
        );
        return ResponseEntity.accepted().build();
    }
}
