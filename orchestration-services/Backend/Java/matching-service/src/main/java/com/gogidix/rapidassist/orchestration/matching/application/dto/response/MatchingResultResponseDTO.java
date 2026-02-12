package com.gogidix.rapidassist.orchestration.matching.application.dto.response;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingAlgorithm;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResultResponseDTO {

    private String id;
    private String requestId;
    private String incidentId;
    private MatchingAlgorithm algorithm;
    private ProviderMatchDTO topProvider;
    private List<ProviderMatchDTO> allProviders;
    private Integer totalProviders;
    private Double processingTimeMs;
    private LocalDateTime matchedAt;
    private String status;
    private String statusMessage;
    private LocalDateTime expiresAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderMatchDTO {
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
        private Integer rank;
        private Double distanceScore;
        private Double capabilityScore;
        private Double availabilityScore;
        private Double ratingScore;
        private Double costScore;
    }
}
