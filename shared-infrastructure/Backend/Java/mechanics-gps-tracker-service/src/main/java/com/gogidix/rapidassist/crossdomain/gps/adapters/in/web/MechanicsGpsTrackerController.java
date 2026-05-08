package com.gogidix.rapidassist.crossdomain.gps.adapters.in.web;

import com.gogidix.rapidassist.crossdomain.gps.application.MechanicsGpsTrackerService;
import com.gogidix.rapidassist.crossdomain.gps.domain.model.GpsLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bridge/gps-tracker")
@Slf4j
@RequiredArgsConstructor
public class MechanicsGpsTrackerController {

    private final MechanicsGpsTrackerService trackerService;

    // ==================== LOCATION UPDATES ====================

    @PostMapping("/locations")
    public Mono<ResponseEntity<GpsLocation>> updateLocation(@RequestBody GpsLocation location) {
        log.info("Updating location for mechanic {}", location.getMechanicId());

        return trackerService.updateMechanicLocation(location)
            .map(ResponseEntity::ok);
    }

    @GetMapping("/locations")
    public Flux<GpsLocation> getAllLocations() {
        log.info("Fetching all mechanic locations");

        return trackerService.getAllActiveMechanicLocations();
    }

    @GetMapping("/locations/{mechanicId}")
    public Mono<ResponseEntity<GpsLocation>> getLocation(@PathVariable String mechanicId) {
        log.info("Fetching location for mechanic {}", mechanicId);

        return trackerService.getMechanicLocation(mechanicId)
            .map(ResponseEntity::ok);
    }

    // ==================== TRACKING SUMMARY ====================

    @GetMapping("/mechanics/{mechanicId}/tracking-summary")
    public Mono<ResponseEntity<GpsLocation.TrackingSummary>> getTrackingSummary(
        @PathVariable String mechanicId
    ) {
        log.info("Fetching tracking summary for mechanic {}", mechanicId);

        return trackerService.getMechanicTrackingSummary(mechanicId)
            .map(ResponseEntity::ok);
    }

    // ==================== NEARBY MECHANICS ====================

    @GetMapping("/mechanics/nearby")
    public Flux<GpsLocation> getNearbyMechanics(
        @RequestParam BigDecimal latitude,
        @RequestParam BigDecimal longitude,
        @RequestParam(defaultValue = "10") Double radiusKm
    ) {
        log.info("Finding mechanics within {}km of {}, {}", radiusKm, latitude, longitude);

        return trackerService.getNearbyMechanics(latitude, longitude, radiusKm);
    }

    // ==================== JOB TRACKING ====================

    @PostMapping("/mechanics/{mechanicId}/start-tracking")
    public Mono<ResponseEntity<Void>> startTracking(
        @PathVariable String mechanicId,
        @RequestParam String jobId
    ) {
        log.info("Starting tracking for mechanic {} on job {}", mechanicId, jobId);

        return trackerService.startTrackingForJob(mechanicId, jobId)
            .map(ResponseEntity::ok);
    }

    @PostMapping("/mechanics/{mechanicId}/stop-tracking")
    public Mono<ResponseEntity<Void>> stopTracking(@PathVariable String mechanicId) {
        log.info("Stopping tracking for mechanic {}", mechanicId);

        return trackerService.stopTracking(mechanicId)
            .map(ResponseEntity::ok);
    }

    // ==================== HEALTH CHECK ====================

    @GetMapping("/health")
    public ResponseEntity<org.springframework.util.MultiValueMap<String, String>> health() {
        return ResponseEntity.ok(org.springframework.util.LinkedMultiValueMap.builder()
            .add("service", "mechanics-gps-tracker-service")
            .add("status", "UP")
            .add("description", "Cross-domain bridge between Mechanics and Central-Monitoring")
            .add("updateInterval", "30 seconds")
            .build());
    }
}
