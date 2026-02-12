package com.gogidix.rapidassist.ai.riskassessment.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RiskFactor domain model
 */
@DisplayName("RiskFactor Domain Model Tests")
class RiskFactorTest {

    @Test
    @DisplayName("Should create risk factor with builder")
    void shouldCreateRiskFactorWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        UUID riskAssessmentId = UUID.randomUUID();
        String name = "Credit Score";
        String description = "Customer credit history assessment";
        RiskCategory category = RiskCategory.FINANCIAL;
        double weight = 0.3;
        double score = 75.0;
        double impact = 0.8;
        double likelihood = 0.7;
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("source", "credit_bureau");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String createdBy = "user-123";
        String updatedBy = "user-123";

        // When
        RiskFactor riskFactor = RiskFactor.builder()
                .id(id)
                .tenantId(tenantId)
                .riskAssessmentId(riskAssessmentId)
                .name(name)
                .description(description)
                .category(category)
                .weight(weight)
                .score(score)
                .impact(impact)
                .likelihood(likelihood)
                .metadata(metadata)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .build();

        // Then
        assertNotNull(riskFactor);
        assertEquals(id, riskFactor.getId());
        assertEquals(tenantId, riskFactor.getTenantId());
        assertEquals(riskAssessmentId, riskFactor.getRiskAssessmentId());
        assertEquals(name, riskFactor.getName());
        assertEquals(description, riskFactor.getDescription());
        assertEquals(category, riskFactor.getCategory());
        assertEquals(weight, riskFactor.getWeight(), 0.001);
        assertEquals(score, riskFactor.getScore(), 0.001);
        assertEquals(impact, riskFactor.getImpact(), 0.001);
        assertEquals(likelihood, riskFactor.getLikelihood(), 0.001);
        assertEquals(metadata, riskFactor.getMetadata());
    }

    @Test
    @DisplayName("Should calculate weighted score correctly")
    void shouldCalculateWeightedScoreCorrectly() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(75.0)
                .weight(0.3)
                .build();

        // When
        double weightedScore = riskFactor.calculateWeightedScore();

        // Then
        assertEquals(22.5, weightedScore, 0.001);
    }

    @Test
    @DisplayName("Should return true when factor is high risk")
    void shouldReturnTrueWhenFactorIsHighRisk() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(70.0)
                .build();

        // When
        boolean isHighRisk = riskFactor.isHighRisk();

        // Then
        assertTrue(isHighRisk);
    }

    @Test
    @DisplayName("Should return false when factor is not high risk")
    void shouldReturnFalseWhenFactorIsNotHighRisk() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(50.0)
                .build();

        // When
        boolean isHighRisk = riskFactor.isHighRisk();

        // Then
        assertFalse(isHighRisk);
    }

    @Test
    @DisplayName("Should return true when factor is critical")
    void shouldReturnTrueWhenFactorIsCritical() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(85.0)
                .build();

        // When
        boolean isCritical = riskFactor.isCritical();

        // Then
        assertTrue(isCritical);
    }

    @Test
    @DisplayName("Should return false when factor is not critical")
    void shouldReturnFalseWhenFactorIsNotCritical() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(75.0)
                .build();

        // When
        boolean isCritical = riskFactor.isCritical();

        // Then
        assertFalse(isCritical);
    }

    @Test
    @DisplayName("Should update score successfully")
    void shouldUpdateScoreSuccessfully() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(50.0)
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        // When
        riskFactor.updateScore(75.0, "user-123");

        // Then
        assertEquals(75.0, riskFactor.getScore(), 0.001);
        assertEquals("user-123", riskFactor.getUpdatedBy());
        assertTrue(riskFactor.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @DisplayName("Should throw exception when updating score below 0")
    void shouldThrowExceptionWhenUpdatingScoreBelowZero() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(50.0)
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> riskFactor.updateScore(-10.0, "user-123"));
        assertEquals(50.0, riskFactor.getScore(), 0.001);
    }

    @Test
    @DisplayName("Should throw exception when updating score above 100")
    void shouldThrowExceptionWhenUpdatingScoreAbove100() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(50.0)
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> riskFactor.updateScore(150.0, "user-123"));
        assertEquals(50.0, riskFactor.getScore(), 0.001);
    }

    @Test
    @DisplayName("Should get risk level correctly")
    void shouldGetRiskLevelCorrectly() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(75.0)
                .build();

        // When
        RiskLevel riskLevel = riskFactor.getRiskLevel();

        // Then
        assertNotNull(riskLevel);
        assertEquals(RiskLevel.HIGH, riskLevel);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        RiskFactor riskFactor = new RiskFactor();

        // Then
        assertNotNull(riskFactor);
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // When
        RiskFactor riskFactor = new RiskFactor(
                id, "tenant-123", UUID.randomUUID(), "Test Factor", "Description",
                RiskCategory.FINANCIAL, 0.3, 75.0, 0.8, 0.7, null,
                now, now, "user-1", "user-1"
        );

        // Then
        assertEquals(id, riskFactor.getId());
        assertEquals("Test Factor", riskFactor.getName());
    }

    @Test
    @DisplayName("Should handle boundary score of 60")
    void shouldHandleBoundaryScoreOf60() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(60.0)
                .build();

        // When
        boolean isHighRisk = riskFactor.isHighRisk();

        // Then
        assertTrue(isHighRisk);
    }

    @Test
    @DisplayName("Should handle boundary score of 80")
    void shouldHandleBoundaryScoreOf80() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(80.0)
                .build();

        // When
        boolean isCritical = riskFactor.isCritical();

        // Then
        assertTrue(isCritical);
    }

    @Test
    @DisplayName("Should handle zero weight in calculation")
    void shouldHandleZeroWeightInCalculation() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(75.0)
                .weight(0.0)
                .build();

        // When
        double weightedScore = riskFactor.calculateWeightedScore();

        // Then
        assertEquals(0.0, weightedScore, 0.001);
    }

    @Test
    @DisplayName("Should handle score of 0")
    void shouldHandleScoreOfZero() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(0.0)
                .build();

        // When
        RiskLevel riskLevel = riskFactor.getRiskLevel();

        // Then
        assertEquals(RiskLevel.LOW, riskLevel);
    }

    @Test
    @DisplayName("Should handle score of 100")
    void shouldHandleScoreOf100() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(100.0)
                .build();

        // When
        RiskLevel riskLevel = riskFactor.getRiskLevel();

        // Then
        assertEquals(RiskLevel.CRITICAL, riskLevel);
    }

    @Test
    @DisplayName("Should update score to boundary values")
    void shouldUpdateScoreToBoundaryValues() {
        // Given
        RiskFactor riskFactor = RiskFactor.builder()
                .score(50.0)
                .build();

        // When - update to 0
        riskFactor.updateScore(0.0, "user-123");
        assertEquals(0.0, riskFactor.getScore(), 0.001);

        // When - update to 100
        riskFactor.updateScore(100.0, "user-123");
        assertEquals(100.0, riskFactor.getScore(), 0.001);
    }
}
