package com.gogidix.rapidassist.orchestration.matching.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity representing matching configuration criteria
 * Collection: matching_criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "matching_criteria")
public class MatchingCriteria {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String criteriaId;

    @Indexed
    private String name;

    private String description;

    private MatchingAlgorithm defaultAlgorithm;

    private ScoringWeights scoringWeights;

    private GeospatialCriteria geospatialCriteria;

    private CostCriteria costCriteria;

    private AvailabilityCriteria availabilityCriteria;

    private CapabilityCriteria capabilityCriteria;

    @Indexed
    private Boolean isActive;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private String serviceType;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer version;

    /**
     * Scoring weights configuration
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoringWeights {
        private Double distanceWeight;
        private Double capabilityWeight;
        private Double availabilityWeight;
        private Double ratingWeight;
        private Double costWeight;
        private Double priorityWeight;
        private Map<String, Double> customWeights;
    }

    /**
     * Geospatial matching criteria
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeospatialCriteria {
        private Double defaultRadiusKm;
        private Double maxRadiusKm;
        private Boolean requireGeospatial;
        private Double distanceDecayFactor;
    }

    /**
     * Cost-based criteria
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CostCriteria {
        private Double maxCost;
        private Double preferredCostRange;
        private Double costImportance;
        private Boolean considerCost;
    }

    /**
     * Availability criteria
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityCriteria {
        private Boolean requireImmediateAvailability;
        private Integer maxWaitTimeMinutes;
        private Double availabilityThreshold;
        private Boolean considerCurrentLoad;
    }

    /**
     * Capability matching criteria
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapabilityCriteria {
        private Boolean requireAllCapabilities;
        private Boolean allowPartialMatch;
        private Double capabilityMatchThreshold;
        private Integer minCapabilities;
    }
}
