package com.gogidix.rapidassist.orchestration.matching.domain.policy;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain policy for validating matching rules and constraints
 */
@Slf4j
@Component
public class MatchingPolicy {

    /**
     * Validate if a provider meets matching criteria
     */
    public boolean validateProviderMatch(
        ProviderProfile provider,
        MatchingRequest request,
        MatchingCriteria criteria
    ) {
        if (!isProviderAvailable(provider)) {
            log.debug("Provider {} is not available", provider.getProviderId());
            return false;
        }

        if (!validateCapabilities(provider, request, criteria)) {
            log.debug("Provider {} doesn't meet capability requirements", provider.getProviderId());
            return false;
        }

        if (!validateGeospatialConstraints(provider, request, criteria)) {
            log.debug("Provider {} doesn't meet geospatial constraints", provider.getProviderId());
            return false;
        }

        if (!validateCostConstraints(provider, request, criteria)) {
            log.debug("Provider {} doesn't meet cost constraints", provider.getProviderId());
            return false;
        }

        if (!validateRatingConstraints(provider, request)) {
            log.debug("Provider {} doesn't meet rating constraints", provider.getProviderId());
            return false;
        }

        return true;
    }

    /**
     * Check if provider is available for new assignments
     */
    public boolean isProviderAvailable(ProviderProfile provider) {
        if (provider.getStatus() != ProviderProfile.ProviderStatus.AVAILABLE) {
            return false;
        }

        if (!provider.getIsActive()) {
            return false;
        }

        if (provider.getCurrentJobsCount() != null &&
            provider.getMaxConcurrentJobs() != null &&
            provider.getCurrentJobsCount() >= provider.getMaxConcurrentJobs()) {
            return false;
        }

        return true;
    }

    /**
     * Validate provider capabilities
     */
    private boolean validateCapabilities(
        ProviderProfile provider,
        MatchingRequest request,
        MatchingCriteria criteria
    ) {
        if (request.getRequiredCapabilities() == null || request.getRequiredCapabilities().isEmpty()) {
            return true;
        }

        List<String> providerCapabilities = provider.getCapabilities();
        if (providerCapabilities == null || providerCapabilities.isEmpty()) {
            return false;
        }

        if (criteria != null && criteria.getCapabilityCriteria() != null) {
            MatchingCriteria.CapabilityCriteria capabilityCriteria = criteria.getCapabilityCriteria();

            if (capabilityCriteria.getRequireAllCapabilities() != null &&
                capabilityCriteria.getRequireAllCapabilities()) {
                return providerCapabilities.containsAll(request.getRequiredCapabilities());
            }

            if (capabilityCriteria.getMinCapabilities() != null) {
                long matchedCount = request.getRequiredCapabilities().stream()
                    .filter(providerCapabilities::contains)
                    .count();
                return matchedCount >= capabilityCriteria.getMinCapabilities();
            }

            if (capabilityCriteria.getAllowPartialMatch() != null &&
                !capabilityCriteria.getAllowPartialMatch()) {
                return providerCapabilities.containsAll(request.getRequiredCapabilities());
            }
        }

        // Default: require all capabilities
        return providerCapabilities.containsAll(request.getRequiredCapabilities());
    }

    /**
     * Validate geospatial constraints
     */
    private boolean validateGeospatialConstraints(
        ProviderProfile provider,
        MatchingRequest request,
        MatchingCriteria criteria
    ) {
        if (provider.getCurrentLocation() == null || request.getIncidentLocation() == null) {
            return false;
        }

        double distance = calculateDistance(
            provider.getCurrentLocation().getCoordinates()[0],
            provider.getCurrentLocation().getCoordinates()[1],
            request.getIncidentLocation().getCoordinates()[0],
            request.getIncidentLocation().getCoordinates()[1]
        );

        double maxDistance = request.getMaxDistanceKm() != null ?
            request.getMaxDistanceKm() :
            (criteria != null && criteria.getGeospatialCriteria() != null ?
                criteria.getGeospatialCriteria().getMaxRadiusKm() : 200.0);

        return distance <= maxDistance;
    }

    /**
     * Validate cost constraints
     */
    private boolean validateCostConstraints(
        ProviderProfile provider,
        MatchingRequest request,
        MatchingCriteria criteria
    ) {
        if (request.getMaxCost() == null) {
            return true;
        }

        double estimatedCost = estimateCost(provider, request);

        if (criteria != null && criteria.getCostCriteria() != null) {
            Double maxCost = criteria.getCostCriteria().getMaxCost();
            if (maxCost != null) {
                return estimatedCost <= Math.min(request.getMaxCost(), maxCost);
            }
        }

        return estimatedCost <= request.getMaxCost();
    }

    /**
     * Validate rating constraints
     */
    private boolean validateRatingConstraints(
        ProviderProfile provider,
        MatchingRequest request
    ) {
        if (request.getMinProviderRating() == null) {
            return true;
        }

        return provider.getRating() != null &&
               provider.getRating() >= request.getMinProviderRating();
    }

    /**
     * Calculate distance between two coordinates (Haversine formula)
     */
    private double calculateDistance(double lon1, double lat1, double lon2, double lat2) {
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

        double distance = calculateDistance(
            provider.getCurrentLocation().getCoordinates()[0],
            provider.getCurrentLocation().getCoordinates()[1],
            request.getIncidentLocation().getCoordinates()[0],
            request.getIncidentLocation().getCoordinates()[1]
        );

        double distanceCost = provider.getRatePerKm() != null ?
            provider.getRatePerKm() * distance : 0.0;

        return baseCost + distanceCost;
    }

    /**
     * Filter providers based on basic eligibility
     */
    public List<ProviderProfile> filterEligibleProviders(
        List<ProviderProfile> providers,
        MatchingRequest request,
        MatchingCriteria criteria
    ) {
        return providers.stream()
            .filter(provider -> validateProviderMatch(provider, request, criteria))
            .collect(Collectors.toList());
    }
}
