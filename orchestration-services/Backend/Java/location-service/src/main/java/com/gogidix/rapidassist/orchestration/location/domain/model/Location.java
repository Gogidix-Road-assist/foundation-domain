package com.gogidix.rapidassist.orchestration.location.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Location aggregate root for real-time entity tracking
 * Supports vehicles, drivers, requests, and providers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "locations")
public class Location implements Persistable<String> {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private EntityType entityType;

    @Indexed
    private String entityId;

    /**
     * GeoJSON Point format for geospatial queries
     * Format: { "type": "Point", "coordinates": [longitude, latitude] }
     */
    @Indexed
    private Map<String, Object> coordinates;

    private Double latitude;
    private Double longitude;

    private Double altitude;
    private Double accuracy;
    private Double bearing;
    private Double speed;

    @Indexed
    private String address;

    private String city;
    private String state;
    private String country;
    private String postalCode;

    @Indexed
    private LocationStatus status;

    private LocalDateTime timestamp;
    private LocalDateTime lastUpdated;

    @Builder.Default
    private Boolean active = true;

    private Map<String, Object> metadata;
    private String provider; // GPS provider (gps, network, passive)

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }

    /**
     * Update location with new coordinates
     */
    public void updateCoordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.coordinates = Map.of(
            "type", "Point",
            "coordinates", new Double[]{longitude, latitude}
        );
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Check if location is within radius of another point (in meters)
     */
    public boolean isWithinRadius(Double otherLat, Double otherLon, Double radiusInMeters) {
        double distance = calculateDistance(latitude, longitude, otherLat, otherLon);
        return distance <= radiusInMeters;
    }

    /**
     * Calculate distance between two points in meters using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public enum EntityType {
        VEHICLE,
        DRIVER,
        REQUEST,
        PROVIDER
    }

    public enum LocationStatus {
        ACTIVE,
        INACTIVE,
        MOVING,
        IDLE,
        OFFLINE,
        ERROR
    }
}
