package com.gogidix.rapidassist.analytics.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Metric domain model.
 * Tests all business logic, builders, and validation.
 */
@DisplayName("Metric Domain Model Tests")
class MetricDomainModelTest {

    @Test
    @DisplayName("Should create metric with all fields using builder")
    void shouldCreateMetricWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        LocalDateTime now = LocalDateTime.now();

        // When
        Metric metric = Metric.builder()
                .id(id)
                .tenantId(tenantId)
                .metricName("page_views")
                .metricCategory("USAGE")
                .metricValue(1500.0)
                .metricUnit("count")
                .metricType("COUNTER")
                .timestamp(now)
                .dimensions(Map.of("page", "/home"))
                .source("web")
                .granularity("hourly")
                .tags(Map.of("env", "prod"))
                .threshold(2000.0)
                .status("ACTIVE")
                .description("Test metric")
                .metadata(Map.of("version", "1.0"))
                .createdAt(now.minusHours(1))
                .updatedAt(now)
                .build();

        // Then
        assertNotNull(metric);
        assertEquals(id, metric.getId());
        assertEquals(tenantId, metric.getTenantId());
        assertEquals("page_views", metric.getMetricName());
        assertEquals("USAGE", metric.getMetricCategory());
        assertEquals(1500.0, metric.getMetricValue());
        assertEquals("count", metric.getMetricUnit());
        assertEquals("COUNTER", metric.getMetricType());
    }

    @Test
    @DisplayName("Should check if metric exceeds threshold")
    void shouldCheckIfMetricExceedsThreshold() {
        // Given
        Metric metric = Metric.builder()
                .metricValue(1500.0)
                .threshold(1000.0)
                .build();

        // When
        boolean exceeds = metric.exceedsThreshold();

        // Then
        assertTrue(exceeds);
    }

    @Test
    @DisplayName("Should check if metric is below threshold")
    void shouldCheckIfMetricIsBelowThreshold() {
        // Given
        Metric metric = Metric.builder()
                .metricValue(500.0)
                .threshold(1000.0)
                .build();

        // When
        boolean below = metric.isBelowThreshold();

        // Then
        assertTrue(below);
    }

    @Test
    @DisplayName("Should get metric age in hours")
    void shouldGetMetricAgeInHours() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now().minusHours(5);
        Metric metric = Metric.builder()
                .timestamp(timestamp)
                .build();

        // When
        long age = metric.getAgeInHours();

        // Then
        assertEquals(5, age);
    }

    @Test
    @DisplayName("Should check if metric is stale")
    void shouldCheckIfMetricIsStale() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now().minusHours(25);
        Metric metric = Metric.builder()
                .timestamp(timestamp)
                .build();

        // When
        boolean stale = metric.isStale(24);

        // Then
        assertTrue(stale);
    }

    @Test
    @DisplayName("Should update metric value")
    void shouldUpdateMetricValue() {
        // Given
        Metric metric = Metric.builder()
                .metricValue(1000.0)
                .metadata(null)
                .build();

        // When
        metric.updateValue(1500.0, "test-user");

        // Then
        assertEquals(1500.0, metric.getMetricValue());
        assertEquals("UPDATED", metric.getStatus());
        assertNotNull(metric.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle null threshold in exceedsThreshold")
    void shouldHandleNullThresholdInExceedsThreshold() {
        // Given
        Metric metric = Metric.builder()
                .metricValue(1500.0)
                .threshold(null)
                .build();

        // When
        boolean exceeds = metric.exceedsThreshold();

        // Then
        assertFalse(exceeds);
    }

    @Test
    @DisplayName("Should handle null timestamp in getAgeInHours")
    void shouldHandleNullTimestampInGetAgeInHours() {
        // Given
        Metric metric = Metric.builder()
                .timestamp(null)
                .build();

        // When
        long age = metric.getAgeInHours();

        // Then
        assertEquals(0, age);
    }
}
