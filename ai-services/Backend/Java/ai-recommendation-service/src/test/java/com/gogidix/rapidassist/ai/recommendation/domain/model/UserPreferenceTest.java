package com.gogidix.rapidassist.ai.recommendation.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserPreference domain model
 */
@DisplayName("UserPreference Domain Model Tests")
class UserPreferenceTest {

    @Test
    @DisplayName("Should create user preference with builder")
    void shouldCreateUserPreferenceWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        String userId = "user-123";
        String itemType = "product";
        String itemId = "product-123";
        String preferenceKey = "category";
        String preferenceValue = "electronics";
        Double preferenceScore = 0.8;
        Integer interactionCount = 5;
        LocalDateTime lastInteractionAt = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String metadata = "{\"source\":\"click\"}";

        // When
        UserPreference preference = UserPreference.builder()
                .id(id)
                .tenantId(tenantId)
                .userId(userId)
                .itemType(itemType)
                .itemId(itemId)
                .preferenceKey(preferenceKey)
                .preferenceValue(preferenceValue)
                .preferenceScore(preferenceScore)
                .interactionCount(interactionCount)
                .lastInteractionAt(lastInteractionAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .metadata(metadata)
                .build();

        // Then
        assertNotNull(preference);
        assertEquals(id, preference.getId());
        assertEquals(tenantId, preference.getTenantId());
        assertEquals(userId, preference.getUserId());
        assertEquals(itemType, preference.getItemType());
        assertEquals(itemId, preference.getItemId());
        assertEquals(preferenceKey, preference.getPreferenceKey());
        assertEquals(preferenceValue, preference.getPreferenceValue());
        assertEquals(preferenceScore, preference.getPreferenceScore());
        assertEquals(interactionCount, preference.getInteractionCount());
        assertEquals(lastInteractionAt, preference.getLastInteractionAt());
        assertEquals(createdAt, preference.getCreatedAt());
        assertEquals(updatedAt, preference.getUpdatedAt());
        assertEquals(metadata, preference.getMetadata());
    }

    @Test
    @DisplayName("Should update score correctly")
    void shouldUpdateScoreCorrectly() {
        // Given
        UserPreference preference = UserPreference.builder()
                .preferenceScore(0.5)
                .interactionCount(5)
                .lastInteractionAt(LocalDateTime.now().minusDays(1))
                .build();

        // When
        preference.updateScore(0.2);

        // Then
        assertEquals(0.7, preference.getPreferenceScore(), 0.001);
        assertEquals(6, preference.getInteractionCount());
        assertNotNull(preference.getLastInteractionAt());
        assertTrue(preference.getLastInteractionAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @DisplayName("Should handle null initial score when updating")
    void shouldHandleNullInitialScoreWhenUpdating() {
        // Given
        UserPreference preference = UserPreference.builder()
                .preferenceScore(null)
                .interactionCount(null)
                .build();

        // When
        preference.updateScore(0.3);

        // Then
        assertEquals(0.3, preference.getPreferenceScore(), 0.001);
        assertEquals(1, preference.getInteractionCount());
    }

    @Test
    @DisplayName("Should handle null initial interaction count when updating")
    void shouldHandleNullInitialInteractionCountWhenUpdating() {
        // Given
        UserPreference preference = UserPreference.builder()
                .preferenceScore(0.5)
                .interactionCount(null)
                .build();

        // When
        preference.updateScore(0.1);

        // Then
        assertEquals(0.6, preference.getPreferenceScore(), 0.001);
        assertEquals(1, preference.getInteractionCount());
    }

    @Test
    @DisplayName("Should return true when preference is stale")
    void shouldReturnTrueWhenPreferenceIsStale() {
        // Given
        UserPreference preference = UserPreference.builder()
                .lastInteractionAt(LocalDateTime.now().minusDays(10))
                .build();

        // When
        boolean isStale = preference.isStale(7);

        // Then
        assertTrue(isStale);
    }

    @Test
    @DisplayName("Should return false when preference is fresh")
    void shouldReturnFalseWhenPreferenceIsFresh() {
        // Given
        UserPreference preference = UserPreference.builder()
                .lastInteractionAt(LocalDateTime.now().minusDays(3))
                .build();

        // When
        boolean isStale = preference.isStale(7);

        // Then
        assertFalse(isStale);
    }

    @Test
    @DisplayName("Should return true when last interaction is null")
    void shouldReturnTrueWhenLastInteractionIsNull() {
        // Given
        UserPreference preference = UserPreference.builder()
                .lastInteractionAt(null)
                .build();

        // When
        boolean isStale = preference.isStale(7);

        // Then
        assertTrue(isStale);
    }

    @Test
    @DisplayName("Should return normalized score for positive value")
    void shouldReturnNormalizedScoreForPositiveValue() {
        // Given
        UserPreference preference = UserPreference.builder()
                .preferenceScore(0.8)
                .build();

        // When
        Double normalized = preference.getNormalizedScore();

        // Then
        assertEquals(0.8, normalized, 0.001);
    }

    @Test
    @DisplayName("Should return 0.0 for score below 0")
    void shouldReturnZeroForScoreBelowZero() {
        // Given
        UserPreference preference = UserPreference.builder()
                .preferenceScore(-0.5)
                .build();

        // When
        Double normalized = preference.getNormalizedScore();

        // Then
        assertEquals(0.0, normalized, 0.001);
    }

    @Test
    @DisplayName("Should return 1.0 for score above 1")
    void shouldReturnOneForScoreAboveOne() {
        // Given
        UserPreference preference = UserPreference.builder()
                .preferenceScore(1.5)
                .build();

        // When
        Double normalized = preference.getNormalizedScore();

        // Then
        assertEquals(1.0, normalized, 0.001);
    }

    @Test
    @DisplayName("Should return 0.0 for null score")
    void shouldReturnZeroForNullScore() {
        // Given
        UserPreference preference = UserPreference.builder()
                .preferenceScore(null)
                .build();

        // When
        Double normalized = preference.getNormalizedScore();

        // Then
        assertEquals(0.0, normalized, 0.001);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        UserPreference preference = new UserPreference();

        // Then
        assertNotNull(preference);
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // When
        UserPreference preference = new UserPreference(
                id, "tenant-123", "user-123", "product", "product-123",
                "category", "electronics", 0.8, 5, now, now, now, "metadata"
        );

        // Then
        assertEquals(id, preference.getId());
        assertEquals("tenant-123", preference.getTenantId());
        assertEquals("user-123", preference.getUserId());
    }

    @Test
    @DisplayName("Should update timestamp when score is updated")
    void shouldUpdateTimestampWhenScoreIsUpdated() {
        // Given
        LocalDateTime oldTimestamp = LocalDateTime.now().minusHours(1);
        UserPreference preference = UserPreference.builder()
                .updatedAt(oldTimestamp)
                .build();

        // When
        preference.updateScore(0.1);

        // Then
        assertTrue(preference.getUpdatedAt().isAfter(oldTimestamp));
    }

    @Test
    @DisplayName("Should handle negative score delta")
    void shouldHandleNegativeScoreDelta() {
        // Given
        UserPreference preference = UserPreference.builder()
                .preferenceScore(0.8)
                .interactionCount(5)
                .build();

        // When
        preference.updateScore(-0.3);

        // Then
        assertEquals(0.5, preference.getPreferenceScore(), 0.001);
        assertEquals(6, preference.getInteractionCount());
    }
}
