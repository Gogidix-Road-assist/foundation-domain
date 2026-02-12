package com.gogidix.rapidassist.orchestration.matching.domain.policy;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingAlgorithm;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult.ProviderMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * NEAREST matching algorithm
 * Finds the geographically closest providers
 */
@Component
@RequiredArgsConstructor
public class NearestAlgorithmStrategy implements MatchingAlgorithmStrategy {

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
        List<ProviderMatch> scoredProviders = eligibleProviders.stream()
            .map(provider -> scoringEngine.scoreProvider(provider, request, criteria))
            .collect(Collectors.toList());

        // Sort by distance (nearest first)
        List<ProviderMatch> rankedProviders = scoredProviders.stream()
            .sorted(Comparator.comparingDouble(ProviderMatch::getDistanceKm))
            .collect(Collectors.toList());

        // Assign ranks
        for (int i = 0; i < rankedProviders.size(); i++) {
            rankedProviders.get(i).setRank(i + 1);
        }

        ProviderMatch topProvider = rankedProviders.isEmpty() ? null : rankedProviders.get(0);

        return MatchingResult.builder()
            .tenantId(request.getTenantId())
            .requestId(request.getRequestId())
            .incidentId(request.getIncidentId())
            .algorithm(MatchingAlgorithm.NEAREST)
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
        return MatchingAlgorithm.NEAREST;
    }
}
