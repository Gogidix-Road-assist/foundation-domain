package com.gogidix.rapidassist.orchestration.matching.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a provider matching request
 * Collection: matching_requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "matching_requests")
public class MatchingRequest {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String requestId;

    @Indexed
    private String incidentId;

    private MatchingAlgorithm algorithm;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Location incidentLocation;

    private String serviceType;

    private List<String> requiredCapabilities;

    private Integer priority;

    private Double maxDistanceKm;

    private Double maxCost;

    private LocalDateTime requestedTime;

    @Indexed
    private RequestStatus status;

    private LocalDateTime statusChangedAt;

    private String assignedProviderId;

    private String notes;

    private Integer minProviderRating;

    private Boolean requireExactCapabilities;

    private LocalDateTime expiresAt;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer version;

    /**
     * Inner class for location with geospatial support
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double[] coordinates; // [longitude, latitude]
        private String type = "Point";
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

    /**
     * Request status enum
     */
    public enum RequestStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        TIMEOUT
    }
}
