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
 * Entity representing historical matching decisions for analytics
 * Collection: matching_history
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "matching_history")
public class MatchingHistory {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String requestId;

    @Indexed
    private String incidentId;

    private String providerId;

    private String providerName;

    private MatchingAlgorithm algorithm;

    private Double finalScore;

    private Double distanceKm;

    private Double estimatedCost;

    private LocalDateTime matchedAt;

    @Indexed
    private DecisionStatus decisionStatus;

    private String rejectionReason;

    private LocalDateTime providerAcceptedAt;

    private LocalDateTime providerArrivedAt;

    private LocalDateTime serviceCompletedAt;

    private Double actualResponseTimeMinutes;

    private Double actualCost;

    private Integer customerRating;

    private String customerFeedback;

    private Boolean wasSuccessful;

    private String failureReason;

    private Map<String, Object> matchingFactors;

    private Integer providerRank;

    private Integer totalProvidersConsidered;

    private Long processingTimeMs;

    private LocalDateTime createdAt;

    /**
     * Decision status enum
     */
    public enum DecisionStatus {
        MATCHED,
        ACCEPTED,
        REJECTED_BY_PROVIDER,
        REJECTED_BY_CUSTOMER,
        CANCELLED,
        COMPLETED,
        FAILED
    }
}
