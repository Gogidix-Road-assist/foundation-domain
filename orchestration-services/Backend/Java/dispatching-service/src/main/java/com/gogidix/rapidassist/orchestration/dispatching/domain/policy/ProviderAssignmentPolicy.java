package com.gogidix.rapidassist.orchestration.dispatching.domain.policy;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.Dispatch;
import com.gogidix.rapidassist.orchestration.dispatching.domain.model.DispatchProvider;
import org.springframework.stereotype.Component;

/**
 * Policy for determining if a provider can be assigned to a dispatch
 */
@Component
public class ProviderAssignmentPolicy {

    /**
     * Check if provider can be assigned to dispatch
     */
    public boolean canAssign(Dispatch dispatch, DispatchProvider provider) {
        // Provider must be available
        if (provider.getCurrentStatus() != DispatchProvider.ProviderStatus.AVAILABLE) {
            return false;
        }

        // Provider must not be at max capacity
        if (provider.getCurrentLoad() >= provider.getMaxConcurrentJobs()) {
            return false;
        }

        // Provider must support the service type
        if (!provider.getServiceTypes().contains(dispatch.getServiceType())) {
            return false;
        }

        // Provider must be within service area (simplified check)
        if (!isWithinServiceArea(dispatch, provider)) {
            return false;
        }

        return true;
    }

    /**
     * Calculate assignment score for provider
     * Higher score = better match
     */
    public double calculateScore(Dispatch dispatch, DispatchProvider provider) {
        double score = 0.0;

        // Provider rating (0-40 points)
        if (provider.getAverageRating() != null) {
            score += (provider.getAverageRating() / 5.0) * 40;
        }

        // Completion rate (0-30 points)
        if (provider.getTotalCompletedJobs() > 0) {
            double completionRate = (double) provider.getTotalCompletedJobs() /
                                   (provider.getTotalCompletedJobs() + provider.getCurrentLoad());
            score += completionRate * 30;
        }

        // Current load (0-30 points) - lower load = higher score
        int availableCapacity = provider.getMaxConcurrentJobs() - provider.getCurrentLoad();
        double loadScore = ((double) availableCapacity / provider.getMaxConcurrentJobs()) * 30;
        score += loadScore;

        return score;
    }

    private boolean isWithinServiceArea(Dispatch dispatch, DispatchProvider provider) {
        // Simplified check - in production would use proper geospatial calculation
        if (provider.getServiceArea() == null) {
            return true; // No service area restriction
        }

        // TODO: Implement proper geospatial check
        return true;
    }
}
