package com.gogidix.rapidassist.orchestration.location.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Route definition and calculation result
 * Stores optimized paths between locations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "routes")
public class Route {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private RouteStatus status;

    @Indexed
    private String referenceId; // Request ID, Trip ID, etc.

    /**
     * Starting point
     */
    private RoutePoint startPoint;

    /**
     * Ending point
     */
    private RoutePoint endPoint;

    /**
     * Intermediate waypoints
     */
    private List<RoutePoint> waypoints;

    /**
     * Complete route path (polyline)
     * GeoJSON LineString: { "type": "LineString", "coordinates": [[lon1, lat1], [lon2, lat2], ...] }
     */
    private Map<String, Object> geometry;

    /**
     * Individual route steps with navigation instructions
     */
    private List<RouteStep> steps;

    private Double totalDistance; // meters
    private Long totalDuration; // seconds
    private Long estimatedTimeOfArrival; // epoch timestamp

    private String trafficCondition; // LIGHT, MODERATE, HEAVY
    private Double trafficDelay; // seconds

    private String routeType; // FASTEST, SHORTEST, AVOID_TOLLS
    private String provider; // google, osrm, mapbox

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private Map<String, Object> metadata;
    private String polyline; // Encoded polyline string

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoutePoint {
        private Double latitude;
        private Double longitude;
        private String name;
        private String address;
        private LocalDateTime estimatedArrival;
        private Long stopDuration; // seconds
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteStep {
        private Integer stepNumber;
        private String instruction;
        private Double distance; // meters
        private Long duration; // seconds
        private RoutePoint startPoint;
        private RoutePoint endPoint;
        private String maneuver; // turn, merge, etc.
        private List<Double> polyline; // Step-level polyline
    }

    public enum RouteStatus {
        PENDING,
        CALCULATING,
        COMPLETED,
        FAILED,
        EXPIRED
    }

    /**
     * Check if route is still valid
     */
    public boolean isValid() {
        return status == RouteStatus.COMPLETED
                && (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }

    /**
     * Get remaining distance and duration from a specific step
     */
    public Map<String, Object> getRemainingFromStep(Integer stepNumber) {
        if (steps == null || steps.isEmpty()) {
            return Map.of("distance", 0.0, "duration", 0L);
        }

        double remainingDistance = 0.0;
        long remainingDuration = 0L;

        for (int i = stepNumber; i < steps.size(); i++) {
            RouteStep step = steps.get(i);
            remainingDistance += step.getDistance() != null ? step.getDistance() : 0.0;
            remainingDuration += step.getDuration() != null ? step.getDuration() : 0L;
        }

        return Map.of(
            "distance", remainingDistance,
            "duration", remainingDuration
        );
    }
}
