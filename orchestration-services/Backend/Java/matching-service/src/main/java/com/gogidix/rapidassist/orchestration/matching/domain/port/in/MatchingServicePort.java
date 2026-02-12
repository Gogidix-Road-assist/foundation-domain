package com.gogidix.rapidassist.orchestration.matching.domain.port.in;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingAlgorithm;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;

import java.util.List;

/**
 * Input port for matching operations
 * Defines the business contract for matching services
 */
public interface MatchingServicePort {

    /**
     * Find providers for a matching request
     */
    MatchingResult findProviders(MatchingRequest request);

    /**
     * Find providers using a specific algorithm
     */
    MatchingResult findProvidersWithAlgorithm(String requestId, MatchingAlgorithm algorithm);

    /**
     * Re-score existing results
     */
    MatchingResult rescoreProviders(String requestId, MatchingAlgorithm newAlgorithm);

    /**
     * Get matching results by request ID
     */
    MatchingResult getMatchingResult(String requestId);

    /**
     * Cancel a matching request
     */
    void cancelMatchingRequest(String requestId);

    /**
     * Get provider availability
     */
    boolean isProviderAvailable(String providerId);

    /**
     * Batch match multiple requests
     */
    List<MatchingResult> batchMatch(List<MatchingRequest> requests);

    /**
     * Validate matching criteria
     */
    boolean validateMatchingCriteria(MatchingRequest request);
}
