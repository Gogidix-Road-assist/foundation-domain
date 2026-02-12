package com.gogidix.rapidassist.analytics.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Analytics domain model.
 * Tests all business logic, builders, and validation.
 */
@DisplayName("Analytics Domain Model Tests")
class AnalyticsDomainModelTest {

    @Test
    @DisplayName("Should create analytics with all fields using builder")
    void shouldCreateAnalyticsWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        LocalDateTime now = LocalDateTime.now();

        // When
        Analytics analytics = Analytics.builder()
                .id(id)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .dataSource("DATABASE")
                .metrics(Map.of("views", 1000, "clicks", 500))
                .dimensions(Map.of("region", "US", "device", "mobile"))
                .startTime(now.minusDays(1))
                .endTime(now)
                .computedAt(now)
                .status("COMPLETED")
                .totalRecords(1500)
                .aggregationValue(1000.0)
                .aggregationType("SUM")
                .computedBy("system")
                .description("User engagement analytics")
                .metadata(Map.of("version", "1.0"))
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();

        // Then
        assertNotNull(analytics);
        assertEquals(id, analytics.getId());
        assertEquals(tenantId, analytics.getTenantId());
        assertEquals("USER_ENGAGEMENT", analytics.getAnalyticsType());
        assertEquals("DATABASE", analytics.getDataSource());
        assertEquals(2, analytics.getMetrics().size());
        assertEquals(2, analytics.getDimensions().size());
        assertEquals("COMPLETED", analytics.getStatus());
        assertEquals(1500, analytics.getTotalRecords());
        assertEquals(1000.0, analytics.getAggregationValue());
        assertEquals("SUM", analytics.getAggregationType());
    }

    @Test
    @DisplayName("Should create analytics with no-args constructor")
    void shouldCreateAnalyticsWithNoArgsConstructor() {
        // When
        Analytics analytics = new Analytics();

        // Then
        assertNotNull(analytics);
        assertNull(analytics.getId());
        assertNull(analytics.getTenantId());
        assertNull(analytics.getStatus());
    }

    @Test
    @DisplayName("Should create analytics with all-args constructor")
    void shouldCreateAnalyticsWithAllArgsConstructor() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        LocalDateTime now = LocalDateTime.now();

        // When
        Analytics analytics = new Analytics(
                id, tenantId, "USER_ENGAGEMENT", "DATABASE",
                Map.of("views", 1000), Map.of("region", "US"),
                now.minusDays(1), now, now, "COMPLETED",
                1500, 1000.0, "SUM", "system", "Test analytics",
                Map.of("version", "1.0"), now.minusDays(1), now
        );

        // Then
        assertNotNull(analytics);
        assertEquals(id, analytics.getId());
        assertEquals(tenantId, analytics.getTenantId());
        assertEquals("USER_ENGAGEMENT", analytics.getAnalyticsType());
    }

    @Test
    @DisplayName("Should return true when analytics is expired (endTime + 30 days)")
    void shouldReturnTrueWhenAnalyticsIsExpired() {
        // Given
        Analytics analytics = Analytics.builder()
                .endTime(LocalDateTime.now().minusDays(31))
                .build();

        // When
        boolean isExpired = analytics.isExpired();

        // Then
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("Should return false when analytics is not expired")
    void shouldReturnFalseWhenAnalyticsIsNotExpired() {
        // Given
        Analytics analytics = Analytics.builder()
                .endTime(LocalDateTime.now().minusDays(10))
                .build();

        // When
        boolean isExpired = analytics.isExpired();

        // Then
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Should return false when endTime is null (isExpired variant)")
    void shouldReturnFalseWhenEndTimeIsNullVariant() {
        // Given
        Analytics analytics = Analytics.builder()
                .endTime(null)
                .build();

        // When
        boolean isExpired = analytics.isExpired();

        // Then
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Should return correct age in days")
    void shouldReturnCorrectAgeInDays() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(5);
        Analytics analytics = Analytics.builder()
                .createdAt(createdAt)
                .build();

        // When
        long ageInDays = analytics.getAgeInDays();

        // Then
        assertEquals(5, ageInDays);
    }

    @Test
    @DisplayName("Should return zero age when createdAt is null")
    void shouldReturnZeroAgeWhenCreatedAtIsNull() {
        // Given
        Analytics analytics = Analytics.builder()
                .createdAt(null)
                .build();

        // When
        long ageInDays = analytics.getAgeInDays();

        // Then
        assertEquals(0, ageInDays);
    }

    @Test
    @DisplayName("Should return true when analytics is ready for computation")
    void shouldReturnTrueWhenReadyForComputation() {
        // Given
        Analytics analytics = Analytics.builder()
                .status("PENDING")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .build();

        // When
        boolean isReady = analytics.isReadyForComputation();

        // Then
        assertTrue(isReady);
    }

    @Test
    @DisplayName("Should return false when analytics status is not PENDING")
    void shouldReturnFalseWhenStatusIsNotPending() {
        // Given
        Analytics analytics = Analytics.builder()
                .status("COMPLETED")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .build();

        // When
        boolean isReady = analytics.isReadyForComputation();

        // Then
        assertFalse(isReady);
    }

    @Test
    @DisplayName("Should return false when startTime is null")
    void shouldReturnFalseWhenStartTimeIsNull() {
        // Given
        Analytics analytics = Analytics.builder()
                .status("PENDING")
                .startTime(null)
                .endTime(LocalDateTime.now())
                .build();

        // When
        boolean isReady = analytics.isReadyForComputation();

        // Then
        assertFalse(isReady);
    }

    @Test
    @DisplayName("Should return false when endTime is null")
    void shouldReturnFalseWhenEndTimeIsNull() {
        // Given
        Analytics analytics = Analytics.builder()
                .status("PENDING")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(null)
                .build();

        // When
        boolean isReady = analytics.isReadyForComputation();

        // Then
        assertFalse(isReady);
    }

    @Test
    @DisplayName("Should mark analytics as computed successfully")
    void shouldMarkAsComputedSuccessfully() {
        // Given
        Analytics analytics = Analytics.builder()
                .status("PENDING")
                .computedAt(null)
                .aggregationValue(null)
                .updatedAt(null)
                .build();
        Double computedValue = 1500.0;

        // When
        analytics.markAsComputed(computedValue);

        // Then
        assertEquals("COMPLETED", analytics.getStatus());
        assertNotNull(analytics.getComputedAt());
        assertEquals(computedValue, analytics.getAggregationValue());
        assertNotNull(analytics.getUpdatedAt());
    }

    @Test
    @DisplayName("Should mark analytics as failed with reason")
    void shouldMarkAsFailedWithReason() {
        // Given
        Analytics analytics = Analytics.builder()
                .status("PENDING")
                .metadata(null)
                .updatedAt(null)
                .build();
        String failureReason = "Insufficient data";

        // When
        analytics.markAsFailed(failureReason);

        // Then
        assertEquals("FAILED", analytics.getStatus());
        assertNotNull(analytics.getUpdatedAt());
        assertNotNull(analytics.getMetadata());
        assertTrue(analytics.getMetadata().containsKey("failureReason"));
        assertEquals(failureReason, analytics.getMetadata().get("failureReason"));
        assertTrue(analytics.getMetadata().containsKey("failedAt"));
    }

    @Test
    @DisplayName("Should preserve existing metadata when marking as failed")
    void shouldPreserveExistingMetadataWhenMarkingAsFailed() {
        // Given
        Analytics analytics = Analytics.builder()
                .status("PENDING")
                .metadata(Map.of("existingKey", "existingValue"))
                .updatedAt(null)
                .build();
        String failureReason = "Computation error";

        // When
        analytics.markAsFailed(failureReason);

        // Then
        assertEquals("FAILED", analytics.getStatus());
        assertNotNull(analytics.getMetadata());
        assertTrue(analytics.getMetadata().containsKey("failureReason"));
        assertTrue(analytics.getMetadata().containsKey("failedAt"));
    }

    @Test
    @DisplayName("Lombok @Data should generate proper getters and setters")
    void shouldHaveProperGettersAndSetters() {
        // Given
        Analytics analytics = new Analytics();
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-456";

        // When
        analytics.setId(id);
        analytics.setTenantId(tenantId);
        analytics.setStatus("PROCESSING");

        // Then
        assertEquals(id, analytics.getId());
        assertEquals(tenantId, analytics.getTenantId());
        assertEquals("PROCESSING", analytics.getStatus());
    }

    @Test
    @DisplayName("Lombok @Data should generate proper equals and hashCode")
    void shouldHaveProperEqualsAndHashCode() {
        // Given
        UUID id = UUID.randomUUID();
        Analytics analytics1 = Analytics.builder()
                .id(id)
                .tenantId("tenant-123")
                .build();
        Analytics analytics2 = Analytics.builder()
                .id(id)
                .tenantId("tenant-123")
                .build();
        Analytics analytics3 = Analytics.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-456")
                .build();

        // Then
        assertEquals(analytics1, analytics2);
        assertEquals(analytics1.hashCode(), analytics2.hashCode());
        assertNotEquals(analytics1, analytics3);
        assertNotEquals(analytics1.hashCode(), analytics3.hashCode());
    }

    @Test
    @DisplayName("Lombok @Data should generate proper toString")
    void shouldHaveProperToString() {
        // Given
        UUID id = UUID.randomUUID();
        Analytics analytics = Analytics.builder()
                .id(id)
                .tenantId("tenant-123")
                .status("COMPLETED")
                .build();

        // When
        String toString = analytics.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Analytics"));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains("tenant-123"));
        assertTrue(toString.contains("COMPLETED"));
    }

    @Test
    @DisplayName("Should handle empty metrics and dimensions")
    void shouldHandleEmptyMetricsAndDimensions() {
        // Given
        Analytics analytics = Analytics.builder()
                .metrics(Map.of())
                .dimensions(Map.of())
                .build();

        // Then
        assertNotNull(analytics.getMetrics());
        assertNotNull(analytics.getDimensions());
        assertTrue(analytics.getMetrics().isEmpty());
        assertTrue(analytics.getDimensions().isEmpty());
    }

    @Test
    @DisplayName("Should handle null metrics and dimensions")
    void shouldHandleNullMetricsAndDimensions() {
        // Given
        Analytics analytics = Analytics.builder()
                .metrics(null)
                .dimensions(null)
                .build();

        // Then
        assertNull(analytics.getMetrics());
        assertNull(analytics.getDimensions());
    }

    @Test
    @DisplayName("Should handle various aggregation types")
    void shouldHandleVariousAggregationTypes() {
        // Given
        Analytics sumAnalytics = Analytics.builder()
                .aggregationType("SUM")
                .aggregationValue(1000.0)
                .build();
        Analytics avgAnalytics = Analytics.builder()
                .aggregationType("AVERAGE")
                .aggregationValue(500.0)
                .build();
        Analytics countAnalytics = Analytics.builder()
                .aggregationType("COUNT")
                .aggregationValue(1500.0)
                .build();

        // Then
        assertEquals("SUM", sumAnalytics.getAggregationType());
        assertEquals(1000.0, sumAnalytics.getAggregationValue());
        assertEquals("AVERAGE", avgAnalytics.getAggregationType());
        assertEquals(500.0, avgAnalytics.getAggregationValue());
        assertEquals("COUNT", countAnalytics.getAggregationType());
        assertEquals(1500, countAnalytics.getAggregationValue());
    }

    @Test
    @DisplayName("Should handle complex metadata structure")
    void shouldHandleComplexMetadataStructure() {
        // Given
        Map<String, Object> metadata = Map.of(
                "version", "2.0",
                "computedBy", "system",
                "processingTime", 1500L,
                "accuracy", 0.95
        );

        // When
        Analytics analytics = Analytics.builder()
                .metadata(metadata)
                .build();

        // Then
        assertNotNull(analytics.getMetadata());
        assertEquals("2.0", analytics.getMetadata().get("version"));
        assertEquals("system", analytics.getMetadata().get("computedBy"));
        assertEquals(1500L, analytics.getMetadata().get("processingTime"));
        assertEquals(0.95, analytics.getMetadata().get("accuracy"));
    }
}
