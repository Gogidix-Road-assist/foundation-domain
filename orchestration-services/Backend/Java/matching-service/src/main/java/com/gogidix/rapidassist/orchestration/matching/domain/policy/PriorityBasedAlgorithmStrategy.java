package com.gogidix.rapidassist.orchestration.matching.domain.policy;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingAlgorithm;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PRIORITY_BASED matching algorithm
 * Finds providers based on priority levels and availability
 */
@Component
@RequiredArgsConstructor
public class PriorityBasedAlgorithmStrategy implements MatchingAlgorithmStrategy {

    private final MatchingPolicy matchingPolicy;
    private final ScoringEngine scoringEngine;

    @Override
    public MatchingResult match(
        MatchingRequest request,
        List<ProviderProfile> providers,
        MatchingCriteria criteria
    ) {
        long startTime = System.currentTimeMillis();

        // Filter eligible providers
        List<ProviderProfile> eligibleProviders = matchingPolicy.filterEligibleProviders(
            providers,
            request,
            criteria
        );

        // Score providers
        List<MatchingResult.ProviderMatch> scoredProviders = eligibleProviders.stream()
            .map(provider -> scoringEngine.scoreProvider(provider, request, criteria))
            .collect(Collectors.toList());

        // Sort by priority (highest first), then by score
        Comparator<MatchingResult.ProviderMatch> priorityComparator = Comparator
            .comparing((MatchingResult.ProviderMatch p) ->
                getProviderPriority(p.getProviderId(), eligibleProviders))
            .reversed()
            .thenComparing(MatchingResult.ProviderMatch::getScore)
            .reversed();

        List<MatchingResult.ProviderMatch> rankedProviders = scoredProviders.stream()
            .sorted(priorityComparator)
            .collect(Collectors.toList());

        // Assign ranks
        for (int i = 0; i < rankedProviders.size(); i++) {
            rankedProviders.get(i).setRank(i + 1);
        }

        MatchingResult.ProviderMatch topProvider = rankedProviders.isEmpty() ? null : rankedProviders.get(0);

        return MatchingResult.builder()
            .tenantId(request.getTenantId())
            .requestId(request.getRequestId())
            .incidentId(request.getIncidentId())
            .algorithm(MatchingAlgorithm.PRIORITY_BASED)
            .topProvider(topProvider)
            .allProviders(rankedProviders)
            .totalProviders(rankedProviders.size())
            .processingTimeMs((double) (System.currentTimeMillis() - startTime))
            .matchedAt(LocalDateTime.now())
            .status(rankedProviders.isEmpty() ? MatchingResult.ResultStatus.NO_PROVIDERS_FOUND : MatchingResult.ResultStatus.SUCCESS)
            .statusMessage(rankedProviders.isEmpty() ? "No eligible providers found" : "Matching completed successfully")
            .expiresAt(request.getExpiresAt())
            .isExpired(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Override
    public MatchingAlgorithm getAlgorithmType() {
        return MatchingAlgorithm.PRIORITY_BASED;
    }

    /**
     * Get provider priority level
     */
    private Integer getProviderPriority(String providerId, List<ProviderProfile> providers) {
        return providers.stream()
            .filter(p -> p.getProviderId().equals(providerId))
            .findFirst()
            .map(ProviderProfile::getPriorityLevel)
            .orElse(0);
    }
}
