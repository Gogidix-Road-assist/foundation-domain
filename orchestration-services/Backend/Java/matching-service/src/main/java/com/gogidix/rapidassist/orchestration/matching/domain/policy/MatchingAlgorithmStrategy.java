package com.gogidix.rapidassist.orchestration.matching.domain.policy;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingAlgorithm;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult.ProviderMatch;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy interface for matching algorithms
 */
public interface MatchingAlgorithmStrategy {

    /**
     * Execute matching algorithm
     */
    MatchingResult match(
        MatchingRequest request,
        List<ProviderProfile> providers,
        MatchingCriteria criteria
    );

    /**
     * Get the algorithm type
     */
    MatchingAlgorithm getAlgorithmType();
}
