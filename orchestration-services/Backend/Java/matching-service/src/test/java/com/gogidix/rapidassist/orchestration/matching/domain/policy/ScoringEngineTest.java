package com.gogidix.rapidassist.orchestration.matching.domain.policy;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoringEngineTest {

    private ScoringEngine scoringEngine;
    private MatchingRequest request;
    private ProviderProfile provider;
    private MatchingCriteria criteria;

    @BeforeEach
    void setUp() {
        scoringEngine = new ScoringEngine();

        request = MatchingRequest.builder()
            .requestId("req-123")
            .incidentId("incident-123")
            .requiredCapabilities(List.of("TOWING", "TIRE_CHANGE"))
            .priority(5)
            .maxDistanceKm(100.0)
            .maxCost(500.0)
            .incidentLocation(MatchingRequest.Location.builder()
                .coordinates(new Double[]{-73.9857, 40.7484}) // New York
                .build())
            .build();

        provider = ProviderProfile.builder()
            .providerId("prov-123")
            .providerName("Test Provider")
            .capabilities(List.of("TOWING", "TIRE_CHANGE", "JUMP_START"))
            .status(ProviderProfile.ProviderStatus.AVAILABLE)
            .isActive(true)
            .rating(4.5)
            .currentJobsCount(1)
            .maxConcurrentJobs(5)
            .baseRate(50.0)
            .ratePerKm(2.0)
            .priorityLevel(5)
            .currentLocation(ProviderProfile.Location.builder()
                .coordinates(new Double[]{-73.9857, 40.7484})
                .build())
            .build();

        criteria = MatchingCriteria.builder()
            .scoringWeights(MatchingCriteria.ScoringWeights.builder()
                .distanceWeight(0.3)
                .capabilityWeight(0.3)
                .availabilityWeight(0.2)
                .ratingWeight(0.2)
                .build())
            .build();
    }

    @Test
    void scoreProvider_WithValidData_ReturnsProviderMatch() {
        // Act
        var result = scoringEngine.scoreProvider(provider, request, criteria);

        // Assert
        assertNotNull(result);
        assertEquals("prov-123", result.getProviderId());
        assertEquals("Test Provider", result.getProviderName());
        assertNotNull(result.getScore());
        assertTrue(result.getScore() >= 0 && result.getScore() <= 1);
        assertTrue(result.getIsAvailable());
        assertEquals(2, result.getMatchedCapabilities().size());
        assertTrue(result.getMatchedCapabilities().contains("TOWING"));
        assertTrue(result.getMatchedCapabilities().contains("TIRE_CHANGE"));
        assertTrue(result.getMissingCapabilities().isEmpty());
    }

    @Test
    void scoreProvider_WithPartialCapabilities_ReturnsPartialScore() {
        // Arrange
        provider.setCapabilities(List.of("TOWING"));

        // Act
        var result = scoringEngine.scoreProvider(provider, request, criteria);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMatchedCapabilities().size());
        assertEquals(1, result.getMissingCapabilities().size());
        assertTrue(result.getMissingCapabilities().contains("TIRE_CHANGE"));
        assertTrue(result.getCapabilityScore() < 1.0);
    }

    @Test
    void scoreProvider_WithBusyProvider_ReturnsLowerAvailabilityScore() {
        // Arrange
        provider.setCurrentJobsCount(4);
        provider.setMaxConcurrentJobs(5);

        // Act
        var result = scoringEngine.scoreProvider(provider, request, criteria);

        // Assert
        assertNotNull(result);
        assertTrue(result.getAvailabilityScore() < 0.5);
    }

    @Test
    void normalizeScores_WithMultipleProviders_ReturnsNormalizedScores() {
        // Arrange
        var match1 = scoringEngine.scoreProvider(provider, request, criteria);
        var match2 = scoringEngine.scoreProvider(provider, request, criteria);

        // Act
        var normalized = scoringEngine.normalizeScores(List.of(match1, match2));

        // Assert
        assertNotNull(normalized);
        assertEquals(2, normalized.size());
    }

    @Test
    void assignRanks_WithMultipleProviders_ReturnsRankedProviders() {
        // Arrange
        var match1 = scoringEngine.scoreProvider(provider, request, criteria);
        var match2 = scoringEngine.scoreProvider(provider, request, criteria);

        // Act
        var ranked = scoringEngine.assignRanks(List.of(match1, match2));

        // Assert
        assertNotNull(ranked);
        assertEquals(2, ranked.size());
        assertNotNull(ranked.get(0).getRank());
        assertTrue(ranked.get(0).getRank() >= 1);
    }
}
