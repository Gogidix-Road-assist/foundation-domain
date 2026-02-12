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
import java.util.Map;

/**
 * Entity representing a provider's matching profile
 * Collection: provider_profiles
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "provider_profiles")
public class ProviderProfile {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String providerId;

    @Indexed
    private String providerName;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private MatchingRequest.Location currentLocation;

    private List<String> capabilities;

    private List<ServiceArea> serviceAreas;

    @Indexed
    private ProviderStatus status;

    private Double baseRate;

    private Double ratePerKm;

    private Double rating;

    private Integer totalJobs;

    private Integer successfulJobs;

    private Double successRate;

    private LocalDateTime nextAvailableTime;

    private Integer currentJobsCount;

    private Integer maxConcurrentJobs;

    private Double averageResponseTimeMinutes;

    private Double averageCompletionTimeMinutes;

    private Map<String, Double> capabilitySpecializationScores;

    private Integer priorityLevel;

    private Boolean isVerified;

    private LocalDateTime lastActiveAt;

    private List<String> unavailableReasons;

    private Map<String, String> attributes;

    @Indexed
    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer version;

    /**
     * Service area definition
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceArea {
        private String areaId;
        private String name;
        private List<String> postalCodes;
        private Double radiusKm;
        private Double[] centerCoordinates;
        private Boolean isActive;
    }

    /**
     * Provider status enum
     */
    public enum ProviderStatus {
        AVAILABLE,
        BUSY,
        UNAVAILABLE,
        OFFLINE,
        MAINTENANCE
    }
}
