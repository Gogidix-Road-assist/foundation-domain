package com.gogidix.rapidassist.orchestration.matching.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing the result of a matching operation
 * Collection: matching_results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "matching_results")
public class MatchingResult {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String requestId;

    @Indexed
    private String incidentId;

    private MatchingAlgorithm algorithm;

    @Indexed
    private ProviderMatch topProvider;

    private List<ProviderMatch> allProviders;

    private Integer totalProviders;

    private Double processingTimeMs;

    private LocalDateTime matchedAt;

    @Indexed
    private ResultStatus status;

    private String statusMessage;

    private LocalDateTime expiresAt;

    private Boolean isExpired;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Provider match details with score
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderMatch {
        private String providerId;
        private String providerName;
        private Double score;
        private Double distanceKm;
        private Double estimatedCost;
        private Integer rating;
        private LocalDateTime estimatedArrival;
        private List<String> matchedCapabilities;
        private List<String> missingCapabilities;
        private Boolean isAvailable;
        private Double availabilityScore;
        private Double distanceScore;
        private Double capabilityScore;
        private Double ratingScore;
        private Double costScore;
        private Integer rank;
        private String rejectionReason;
    }

    /**
     * Result status enum
     */
    public enum ResultStatus {
        SUCCESS,
        NO_PROVIDERS_FOUND,
        PARTIAL_MATCH,
        ERROR,
        EXPIRED
    }
}
