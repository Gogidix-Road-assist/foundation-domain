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
 * BEST_FIT matching algorithm
 * Finds providers with the best overall fit using weighted scoring
 */
@Component
@RequiredArgsConstructor
public class BestFitAlgorithmStrategy implements MatchingAlgorithmStrategy {

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

        // Score providers using comprehensive scoring
        List<ProviderMatch> scoredProviders = eligibleProviders.stream()
            .map(provider -> scoringEngine.scoreProvider(provider, request, criteria))
            .collect(Collectors.toList());

        // Normalize scores
        List<ProviderMatch> normalizedProviders = scoringEngine.normalizeScores(scoredProviders);

        // Sort by score (highest first)
        List<ProviderMatch> rankedProviders = normalizedProviders.stream()
            .sorted(Comparator.comparingDouble(ProviderMatch::getScore).reversed())
            .collect(Collectors.toList());

        // Assign ranks
        rankedProviders = scoringEngine.assignRanks(rankedProviders);

        ProviderMatch topProvider = rankedProviders.isEmpty() ? null : rankedProviders.get(0);

        return MatchingResult.builder()
            .tenantId(request.getTenantId())
            .requestId(request.getRequestId())
            .incidentId(request.getIncidentId())
            .algorithm(MatchingAlgorithm.BEST_FIT)
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
        return MatchingAlgorithm.BEST_FIT;
    }
}
