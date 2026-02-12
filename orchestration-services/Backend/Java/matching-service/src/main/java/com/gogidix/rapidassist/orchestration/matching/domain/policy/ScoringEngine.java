package com.gogidix.rapidassist.orchestration.matching.domain.policy;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult.ProviderMatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scoring engine for calculating provider match scores
 * Uses weighted scoring algorithm with configurable weights
 */
@Slf4j
@Component
public class ScoringEngine {

    private static final double MAX_DISTANCE_KM = 200.0;
    private static final double MAX_RATING = 5.0;
    private static final double MAX_RESPONSE_TIME_MINUTES = 120.0;

    /**
     * Calculate comprehensive score for provider match
     */
    public ProviderMatch scoreProvider(
        ProviderProfile provider,
        MatchingRequest request,
        MatchingCriteria criteria
    ) {
        MatchingCriteria.ScoringWeights weights = getDefaultWeights();
        if (criteria != null && criteria.getScoringWeights() != null) {
            weights = criteria.getScoringWeights();
        }

        double distanceScore = calculateDistanceScore(provider, request);
        double capabilityScore = calculateCapabilityScore(provider, request);
        double availabilityScore = calculateAvailabilityScore(provider);
        double ratingScore = calculateRatingScore(provider);
        double costScore = calculateCostScore(provider, request, criteria);
        double priorityScore = calculatePriorityScore(provider, request);

        double finalScore = calculateWeightedScore(
            distanceScore,
            capabilityScore,
            availabilityScore,
            ratingScore,
            costScore,
            priorityScore,
            weights
        );

        double distanceKm = calculateDistance(provider, request);
        double estimatedCost = estimateCost(provider, request);
        LocalDateTime estimatedArrival = estimateArrival(provider, request);

        List<String> matchedCapabilities = getMatchedCapabilities(provider, request);
        List<String> missingCapabilities = getMissingCapabilities(provider, request);

        return ProviderMatch.builder()
            .providerId(provider.getProviderId())
            .providerName(provider.getProviderName())
            .score(finalScore)
            .distanceKm(distanceKm)
            .estimatedCost(estimatedCost)
            .rating(provider.getRating() != null ? Integer.valueOf(provider.getRating().intValue()) : null)
            .estimatedArrival(estimatedArrival)
            .matchedCapabilities(matchedCapabilities)
            .missingCapabilities(missingCapabilities)
            .isAvailable(isProviderAvailable(provider))
            .availabilityScore(availabilityScore)
            .distanceScore(distanceScore)
            .capabilityScore(capabilityScore)
            .ratingScore(ratingScore)
            .costScore(costScore)
            .build();
    }

    /**
     * Calculate distance score (higher is better)
     */
    private double calculateDistanceScore(ProviderProfile provider, MatchingRequest request) {
        if (provider.getCurrentLocation() == null || request.getIncidentLocation() == null) {
            return 0.0;
        }

        double distance = calculateDistance(provider, request);
        return Math.max(0, 1 - (distance / MAX_DISTANCE_KM));
    }

    /**
     * Calculate capability score based on matched capabilities
     */
    private double calculateCapabilityScore(ProviderProfile provider, MatchingRequest request) {
        if (request.getRequiredCapabilities() == null || request.getRequiredCapabilities().isEmpty()) {
            return 1.0;
        }

        List<String> providerCapabilities = provider.getCapabilities();
        if (providerCapabilities == null || providerCapabilities.isEmpty()) {
            return 0.0;
        }

        long matchedCount = request.getRequiredCapabilities().stream()
            .filter(providerCapabilities::contains)
            .count();

        return (double) matchedCount / request.getRequiredCapabilities().size();
    }

    /**
     * Calculate availability score
     */
    private double calculateAvailabilityScore(ProviderProfile provider) {
        if (provider.getStatus() != ProviderProfile.ProviderStatus.AVAILABLE) {
            return 0.0;
        }

        if (provider.getCurrentJobsCount() == null || provider.getMaxConcurrentJobs() == null) {
            return 1.0;
        }

        double utilizationRatio = (double) provider.getCurrentJobsCount() / provider.getMaxConcurrentJobs();
        return Math.max(0, 1 - utilizationRatio);
    }

    /**
     * Calculate rating score
     */
    private double calculateRatingScore(ProviderProfile provider) {
        if (provider.getRating() == null) {
            return 0.5; // Neutral score for unrated providers
        }
        return provider.getRating() / MAX_RATING;
    }

    /**
     * Calculate cost score (higher is better = lower cost)
     */
    private double calculateCostScore(
        ProviderProfile provider,
        MatchingRequest request,
        MatchingCriteria criteria
    ) {
        double estimatedCost = estimateCost(provider, request);
        double maxCost = request.getMaxCost() != null ? request.getMaxCost() : Double.MAX_VALUE;

        if (criteria != null && criteria.getCostCriteria() != null &&
            criteria.getCostCriteria().getMaxCost() != null) {
            maxCost = Math.min(maxCost, criteria.getCostCriteria().getMaxCost());
        }

        if (maxCost == 0 || maxCost == Double.MAX_VALUE) {
            return 0.5;
        }

        // Lower cost gets higher score
        return Math.max(0, 1 - (estimatedCost / maxCost));
    }

    /**
     * Calculate priority score
     */
    private double calculatePriorityScore(ProviderProfile provider, MatchingRequest request) {
        if (request.getPriority() == null) {
            return 0.5;
        }

        if (provider.getPriorityLevel() == null) {
            return 0.5;
        }

        // Align provider priority with request priority
        int priorityDiff = Math.abs(request.getPriority() - provider.getPriorityLevel());
        return Math.max(0, 1 - (priorityDiff / 10.0));
    }

    /**
     * Calculate weighted final score
     */
    private double calculateWeightedScore(
        double distanceScore,
        double capabilityScore,
        double availabilityScore,
        double ratingScore,
        double costScore,
        double priorityScore,
        MatchingCriteria.ScoringWeights weights
    ) {
        double distanceWeight = weights.getDistanceWeight() != null ? weights.getDistanceWeight() : 0.3;
        double capabilityWeight = weights.getCapabilityWeight() != null ? weights.getCapabilityWeight() : 0.3;
        double availabilityWeight = weights.getAvailabilityWeight() != null ? weights.getAvailabilityWeight() : 0.2;
        double ratingWeight = weights.getRatingWeight() != null ? weights.getRatingWeight() : 0.2;
        double costWeight = weights.getCostWeight() != null ? weights.getCostWeight() : 0.1;
        double priorityWeight = weights.getPriorityWeight() != null ? weights.getPriorityWeight() : 0.1;

        double totalWeight = distanceWeight + capabilityWeight + availabilityWeight +
                            ratingWeight + costWeight + priorityWeight;

        if (totalWeight == 0) {
            return 0.5;
        }

        double weightedScore = (distanceScore * distanceWeight +
                               capabilityScore * capabilityWeight +
                               availabilityScore * availabilityWeight +
                               ratingScore * ratingWeight +
                               costScore * costWeight +
                               priorityScore * priorityWeight) / totalWeight;

        return Math.max(0, Math.min(1, weightedScore));
    }

    /**
     * Normalize scores using percentile rank
     */
    public List<ProviderMatch> normalizeScores(List<ProviderMatch> providers) {
        if (providers == null || providers.isEmpty()) {
            return providers;
        }

        double[] scores = providers.stream()
            .mapToDouble(ProviderMatch::getScore)
            .toArray();

        if (scores.length == 0) {
            return providers;
        }

        final double maxScore;
        double tempMax = 0;
        for (double score : scores) {
            if (score > tempMax) {
                tempMax = score;
            }
        }
        maxScore = tempMax;

        return providers.stream()
            .map(match -> {
                double normalizedScore = maxScore > 0 ? match.getScore() / maxScore : 0;
                match.setScore(normalizedScore);
                return match;
            })
            .collect(Collectors.toList());
    }

    /**
     * Assign ranks to providers based on scores
     */
    public List<ProviderMatch> assignRanks(List<ProviderMatch> providers) {
        List<ProviderMatch> sorted = providers.stream()
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .collect(Collectors.toList());

        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setRank(i + 1);
        }

        return sorted;
    }

    /**
     * Calculate distance between provider and incident
     */
    private double calculateDistance(ProviderProfile provider, MatchingRequest request) {
        if (provider.getCurrentLocation() == null || request.getIncidentLocation() == null) {
            return Double.MAX_VALUE;
        }

        double lon1 = provider.getCurrentLocation().getCoordinates()[0];
        double lat1 = provider.getCurrentLocation().getCoordinates()[1];
        double lon2 = request.getIncidentLocation().getCoordinates()[0];
        double lat2 = request.getIncidentLocation().getCoordinates()[1];

        final double R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Estimate cost for provider service
     */
    private double estimateCost(ProviderProfile provider, MatchingRequest request) {
        double baseCost = provider.getBaseRate() != null ? provider.getBaseRate() : 0.0;

        if (provider.getCurrentLocation() == null || request.getIncidentLocation() == null) {
            return baseCost;
        }

        double distance = calculateDistance(provider, request);
        double distanceCost = provider.getRatePerKm() != null ?
            provider.getRatePerKm() * distance : 0.0;

        return baseCost + distanceCost;
    }

    /**
     * Estimate arrival time
     */
    private LocalDateTime estimateArrival(ProviderProfile provider, MatchingRequest request) {
        double avgResponseTime = provider.getAverageResponseTimeMinutes() != null ?
            provider.getAverageResponseTimeMinutes() : 30.0;

        return LocalDateTime.now().plusMinutes((long) avgResponseTime);
    }

    /**
     * Get matched capabilities
     */
    private List<String> getMatchedCapabilities(ProviderProfile provider, MatchingRequest request) {
        if (request.getRequiredCapabilities() == null || provider.getCapabilities() == null) {
            return List.of();
        }

        return request.getRequiredCapabilities().stream()
            .filter(provider.getCapabilities()::contains)
            .collect(Collectors.toList());
    }

    /**
     * Get missing capabilities
     */
    private List<String> getMissingCapabilities(ProviderProfile provider, MatchingRequest request) {
        if (request.getRequiredCapabilities() == null || provider.getCapabilities() == null) {
            return List.of();
        }

        return request.getRequiredCapabilities().stream()
            .filter(cap -> !provider.getCapabilities().contains(cap))
            .collect(Collectors.toList());
    }

    /**
     * Check if provider is available
     */
    private boolean isProviderAvailable(ProviderProfile provider) {
        return provider.getStatus() == ProviderProfile.ProviderStatus.AVAILABLE &&
               provider.getIsActive() &&
               (provider.getCurrentJobsCount() == null ||
                provider.getMaxConcurrentJobs() == null ||
                provider.getCurrentJobsCount() < provider.getMaxConcurrentJobs());
    }

    /**
     * Get default scoring weights
     */
    private MatchingCriteria.ScoringWeights getDefaultWeights() {
        return MatchingCriteria.ScoringWeights.builder()
            .distanceWeight(0.3)
            .capabilityWeight(0.3)
            .availabilityWeight(0.2)
            .ratingWeight(0.2)
            .costWeight(0.0)
            .priorityWeight(0.0)
            .build();
    }
}
