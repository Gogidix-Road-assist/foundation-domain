package com.gogidix.rapidassist.orchestration.matching.domain.policy;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingPolicyTest {

    private MatchingPolicy matchingPolicy;
    private MatchingRequest request;
    private ProviderProfile provider;
    private MatchingCriteria criteria;

    @BeforeEach
    void setUp() {
        matchingPolicy = new MatchingPolicy();

        request = MatchingRequest.builder()
            .requestId("req-123")
            .incidentId("incident-123")
            .requiredCapabilities(List.of("TOWING"))
            .maxDistanceKm(100.0)
            .maxCost(500.0)
            .minProviderRating(3)
            .incidentLocation(MatchingRequest.Location.builder()
                .coordinates(new Double[]{-73.9857, 40.7484})
                .build())
            .build();

        provider = ProviderProfile.builder()
            .providerId("prov-123")
            .capabilities(List.of("TOWING", "TIRE_CHANGE"))
            .status(ProviderProfile.ProviderStatus.AVAILABLE)
            .isActive(true)
            .rating(4.5)
            .currentJobsCount(1)
            .maxConcurrentJobs(5)
            .baseRate(50.0)
            .ratePerKm(2.0)
            .currentLocation(ProviderProfile.Location.builder()
                .coordinates(new Double[]{-73.9857, 40.7484})
                .build())
            .build();

        criteria = MatchingCriteria.builder()
            .capabilityCriteria(MatchingCriteria.CapabilityCriteria.builder()
                .requireAllCapabilities(false)
                .build())
            .build();
    }

    @Test
    void validateProviderMatch_WithValidProvider_ReturnsTrue() {
        // Act
        boolean isValid = matchingPolicy.validateProviderMatch(provider, request, criteria);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateProviderMatch_WithUnavailableProvider_ReturnsFalse() {
        // Arrange
        provider.setStatus(ProviderProfile.ProviderStatus.BUSY);

        // Act
        boolean isValid = matchingPolicy.validateProviderMatch(provider, request, criteria);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateProviderMatch_WithMissingCapabilities_ReturnsFalse() {
        // Arrange
        provider.setCapabilities(List.of("TIRE_CHANGE"));

        // Act
        boolean isValid = matchingPolicy.validateProviderMatch(provider, request, criteria);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateProviderMatch_WithLowRating_ReturnsFalse() {
        // Arrange
        provider.setRating(2.0);

        // Act
        boolean isValid = matchingPolicy.validateProviderMatch(provider, request, criteria);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isProviderAvailable_WithAvailableProvider_ReturnsTrue() {
        // Act
        boolean available = matchingPolicy.isProviderAvailable(provider);

        // Assert
        assertTrue(available);
    }

    @Test
    void isProviderAvailable_WithBusyProvider_ReturnsFalse() {
        // Arrange
        provider.setStatus(ProviderProfile.ProviderStatus.BUSY);

        // Act
        boolean available = matchingPolicy.isProviderAvailable(provider);

        // Assert
        assertFalse(available);
    }

    @Test
    void isProviderAvailable_WithFullCapacity_ReturnsFalse() {
        // Arrange
        provider.setCurrentJobsCount(5);
        provider.setMaxConcurrentJobs(5);

        // Act
        boolean available = matchingPolicy.isProviderAvailable(provider);

        // Assert
        assertFalse(available);
    }

    @Test
    void filterEligibleProviders_WithMixedProviders_ReturnsOnlyEligible() {
        // Arrange
        ProviderProfile availableProvider = ProviderProfile.builder()
            .providerId("prov-1")
            .capabilities(List.of("TOWING"))
            .status(ProviderProfile.ProviderStatus.AVAILABLE)
            .isActive(true)
            .rating(4.0)
            .currentJobsCount(1)
            .maxConcurrentJobs(5)
            .currentLocation(ProviderProfile.Location.builder()
                .coordinates(new Double[]{-73.9857, 40.7484})
                .build())
            .build();

        ProviderProfile busyProvider = ProviderProfile.builder()
            .providerId("prov-2")
            .capabilities(List.of("TOWING"))
            .status(ProviderProfile.ProviderStatus.BUSY)
            .isActive(true)
            .rating(4.0)
            .currentLocation(ProviderProfile.Location.builder()
                .coordinates(new Double[]{-73.9857, 40.7484})
                .build())
            .build();

        // Act
        List<ProviderProfile> filtered = matchingPolicy.filterEligibleProviders(
            List.of(availableProvider, busyProvider),
            request,
            criteria
        );

        // Assert
        assertEquals(1, filtered.size());
        assertEquals("prov-1", filtered.get(0).getProviderId());
    }
}
