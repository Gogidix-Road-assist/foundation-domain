package com.gogidix.rapidassist.crossdomain.gps.application;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.gogidix.rapidassist.crossdomain.gps.domain.model.GpsLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MechanicsGpsTrackerService {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.mechanics.mobile-mechanic-service}")
    private String mobileMechanicService;

    @Value("${services.mechanics.staff-management-service}")
    private String staffManagementService;

    @Value("${services.central-monitoring.tracking-api}")
    private String centralMonitoringTracking;

    @Value("${services.central-monitoring.monitoring-api}")
    private String centralMonitoringMonitoring;

    @Value("${gps-tracking.update-interval:30000}")
    private Long updateInterval;

    // In-memory cache of mechanic locations
    private final Map<String, GpsLocation> locationCache = new HashMap<>();

    /**
     * Update mechanic GPS location
     * Called by mobile app when location changes
     */
    public Mono<GpsLocation> updateMechanicLocation(GpsLocation location) {
        log.debug("Updating location for mechanic {}: {}, {}",
            location.getMechanicId(),
            location.getLatitude(),
            location.getLongitude());

        // Update cache
        locationCache.put(location.getMechanicId(), location);

        // Forward to Central Monitoring
        return forwardLocationToCentralMonitoring(location)
            .doOnSuccess(success -> log.info("Location forwarded to central monitoring for mechanic {}",
                location.getMechanicId()))
            .doOnError(error -> log.error("Failed to forward location: {}", error.getMessage()));
    }

    /**
     * Get all active mechanic locations
     */
    public Flux<GpsLocation> getAllActiveMechanicLocations() {
        log.debug("Fetching all active mechanic locations");

        return Flux.fromIterable(locationCache.values())
            .filter(location -> location.getStatus() != GpsLocation.LocationStatus.OFF_DUTY);
    }

    /**
     * Get specific mechanic location
     */
    public Mono<GpsLocation> getMechanicLocation(String mechanicId) {
        log.debug("Fetching location for mechanic {}", mechanicId);

        // Check cache first
        if (locationCache.containsKey(mechanicId)) {
            return Mono.just(locationCache.get(mechanicId));
        }

        // Fetch from staff management service
        return fetchMechanicFromStaffService(mechanicId)
            .map(mechanic -> GpsLocation.builder()
                .mechanicId(mechanicId)
                .mechanicName(mechanic.getName())
                .status(GpsLocation.LocationStatus.OFF_DUTY)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * Batch update locations to Central Monitoring
     * Scheduled to run every 30 seconds
     */
    @Scheduled(fixedRateString = "${gps-tracking.update-interval:30000}")
    public void batchUpdateLocationsToCentralMonitoring() {
        log.info("Batch updating {} mechanic locations to central monitoring",
            locationCache.size());

        if (locationCache.isEmpty()) {
            log.debug("No locations to update");
            return;
        }

        // Create batch
        GpsLocation.LocationBatch batch = GpsLocation.LocationBatch.builder()
            .batchId("BATCH-" + System.currentTimeMillis())
            .timestamp(LocalDateTime.now())
            .locations(List.copyOf(locationCache.values()))
            .build();

        // Send to Central Monitoring
        sendBatchToCentralMonitoring(batch)
            .doOnSuccess(success -> log.info("Batch {} sent successfully to central monitoring",
                batch.getBatchId()))
            .doOnError(error -> log.error("Failed to send batch: {}", error.getMessage()))
            .subscribe();
    }

    /**
     * Get tracking summary for mechanic
     */
    public Mono<GpsLocation.TrackingSummary> getMechanicTrackingSummary(String mechanicId) {
        log.info("Fetching tracking summary for mechanic {}", mechanicId);

        return getMechanicLocation(mechanicId)
            .map(location -> {
                GpsLocation.TrackingSummary summary = GpsLocation.TrackingSummary.builder()
                    .mechanicId(location.getMechanicId())
                    .mechanicName(location.getMechanicName())
                    .lastKnownLocation(location)
                    .lastUpdate(location.getTimestamp())
                    .currentStatus(location.getStatus().name())
                    .currentJobId(location.getCurrentJobId())
                    .distanceTraveledToday(0.0)
                    .jobsCompletedToday(0)
                    .build();

                return summary;
            });
    }

    /**
     * Get nearby mechanics for job assignment
     */
    public Flux<GpsLocation> getNearbyMechanics(
        BigDecimal customerLatitude,
        BigDecimal customerLongitude,
        Double radiusKm
    ) {
        log.info("Finding nearby mechanics within {}km of {}, {}",
            radiusKm, customerLatitude, customerLongitude);

        return getAllActiveMechanicLocations()
            .filter(location -> {
                // Only show available or en_route mechanics
                return location.getStatus() == GpsLocation.LocationStatus.AVAILABLE ||
                       location.getStatus() == GpsLocation.LocationStatus.RETURNING;
            })
            .filter(location -> {
                // Calculate distance
                double distance = calculateDistance(
                    customerLatitude.doubleValue(),
                    customerLongitude.doubleValue(),
                    location.getLatitude().doubleValue(),
                    location.getLongitude().doubleValue()
                );
                return distance <= radiusKm;
            })
            .sort((loc1, loc2) -> {
                // Sort by distance
                double dist1 = calculateDistance(
                    customerLatitude.doubleValue(),
                    customerLongitude.doubleValue(),
                    loc1.getLatitude().doubleValue(),
                    loc1.getLongitude().doubleValue()
                );
                double dist2 = calculateDistance(
                    customerLatitude.doubleValue(),
                    customerLongitude.doubleValue(),
                    loc2.getLatitude().doubleValue(),
                    loc2.getLongitude().doubleValue()
                );
                return Double.compare(dist1, dist2);
            });
    }

    /**
     * Start tracking mechanic for job
     */
    public Mono<Void> startTrackingForJob(String mechanicId, String jobId) {
        log.info("Starting tracking for mechanic {} on job {}", mechanicId, jobId);

        return getMechanicLocation(mechanicId)
            .flatMap(location -> {
                GpsLocation updatedLocation = location.toBuilder()
                    .currentJobId(jobId)
                    .status(GpsLocation.LocationStatus.EN_ROUTE_TO_JOB)
                    .timestamp(LocalDateTime.now())
                    .build();

                return updateMechanicLocation(updatedLocation).then();
            });
    }

    /**
     * Stop tracking mechanic
     */
    public Mono<Void> stopTracking(String mechanicId) {
        log.info("Stopping tracking for mechanic {}", mechanicId);

        return getMechanicLocation(mechanicId)
            .flatMap(location -> {
                GpsLocation updatedLocation = location.toBuilder()
                    .currentJobId(null)
                    .status(GpsLocation.LocationStatus.AVAILABLE)
                    .timestamp(LocalDateTime.now())
                    .build();

                return updateMechanicLocation(updatedLocation).then();
            });
    }

    // ==================== PRIVATE METHODS ====================

    private Mono<GpsLocation> forwardLocationToCentralMonitoring(GpsLocation location) {
        return webClientBuilder.build()
            .post()
            .uri(centralMonitoringTracking + "/api/tracking/locations")
            .bodyValue(location)
            .retrieve()
            .bodyToMono(GpsLocation.class);
    }

    private Mono<Void> sendBatchToCentralMonitoring(GpsLocation.LocationBatch batch) {
        return webClientBuilder.build()
            .post()
            .uri(centralMonitoringTracking + "/api/tracking/locations/batch")
            .bodyValue(batch)
            .retrieve()
            .bodyToMono(Void.class);
    }

    private Mono<MechanicInfo> fetchMechanicFromStaffService(String mechanicId) {
        return webClientBuilder.build()
            .get()
            .uri(staffManagementService + "/api/mechanics/" + mechanicId)
            .retrieve()
            .bodyToMono(MechanicInfo.class);
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    // ==================== DTOs ====================

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class MechanicInfo {
        private String mechanicId;
        private String name;
        private String email;
        private String phone;
        private String specialization;
    }
}
