package com.gogidix.rapidassist.ai.analytics.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Dashboard domain model.
 */
@DisplayName("Dashboard Domain Model Tests")
class DashboardDomainModelTest {

    private final String tenantId = "tenant-123";
    private final UUID testId = UUID.randomUUID();

    @Test
    @DisplayName("Should create Dashboard using builder")
    void shouldCreateDashboardUsingBuilder() {
        // Given
        List<UUID> chartIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        Map<String, Object> filters = new HashMap<>();
        filters.put("dateRange", "last7days");

        // When
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .name("Test Dashboard")
                .description("Test analytics dashboard")
                .chartIds(chartIds)
                .filters(filters)
                .refreshInterval("300")
                .isPublic(true)
                .createdBy("admin")
                .build();

        // Then
        assertNotNull(dashboard);
        assertEquals(testId, dashboard.getId());
        assertEquals(tenantId, dashboard.getTenantId());
        assertEquals("Test Dashboard", dashboard.getName());
        assertEquals(2, dashboard.getChartIds().size());
        assertEquals("300", dashboard.getRefreshInterval());
        assertTrue(dashboard.getIsPublic());
    }

    @Test
    @DisplayName("Should create Dashboard using no-args constructor")
    void shouldCreateDashboardUsingNoArgsConstructor() {
        // When
        Dashboard dashboard = new Dashboard();

        // Then
        assertNotNull(dashboard);
        assertNull(dashboard.getId());
        assertNull(dashboard.getTenantId());
        assertNull(dashboard.getName());
        assertNull(dashboard.getDescription());
        assertNull(dashboard.getChartIds());
        assertNull(dashboard.getFilters());
        assertNull(dashboard.getRefreshInterval());
        assertNull(dashboard.getIsPublic());
        assertNull(dashboard.getCreatedBy());
        assertNull(dashboard.getLayout());
    }

    @Test
    @DisplayName("Should create Dashboard with all fields using builder")
    void shouldCreateDashboardWithAllFields() {
        // Given
        List<UUID> chartIds = List.of(UUID.randomUUID());
        Map<String, Object> filters = new HashMap<>();
        Map<String, Object> metadata = new HashMap<>();
        List<String> tags = List.of("sales", "analytics");
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .name("Dashboard 1")
                .description("Description")
                .layout("grid")
                .theme("dark")
                .isPublic(true)
                .createdBy("admin")
                .updatedBy("user")
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .chartIds(chartIds)
                .filters(filters)
                .refreshInterval("300")
                .autoRefresh(true)
                .metadata(metadata)
                .tags(tags)
                .isActive(true)
                .displayOrder(1)
                .category("Analytics")
                .build();

        // Then
        assertNotNull(dashboard);
        assertEquals(testId, dashboard.getId());
        assertEquals("Dashboard 1", dashboard.getName());
        assertEquals("grid", dashboard.getLayout());
        assertEquals("dark", dashboard.getTheme());
        assertEquals(chartIds, dashboard.getChartIds());
        assertEquals(tags, dashboard.getTags());
        assertEquals("Analytics", dashboard.getCategory());
    }

    @Test
    @DisplayName("Should update dashboard filters using setter")
    void shouldUpdateDashboardFilters() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .filters(new HashMap<>())
                .build();

        Map<String, Object> newFilters = new HashMap<>();
        newFilters.put("dateRange", "last30days");
        newFilters.put("region", "US");

        // When
        dashboard.setFilters(newFilters);

        // Then
        assertEquals(newFilters, dashboard.getFilters());
        assertEquals(2, dashboard.getFilters().size());
    }

    @Test
    @DisplayName("Should update dashboard layout using setter")
    void shouldUpdateDashboardLayout() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .layout("old-layout")
                .build();

        // When
        dashboard.setLayout("new-layout");

        // Then
        assertEquals("new-layout", dashboard.getLayout());
    }

    @Test
    @DisplayName("Should update isPublic flag using setter")
    void shouldUpdateIsPublicFlag() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .isPublic(false)
                .build();

        // When
        dashboard.setIsPublic(true);

        // Then
        assertTrue(dashboard.getIsPublic());
    }

    @Test
    @DisplayName("Should update refresh interval using setter")
    void shouldUpdateRefreshInterval() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .refreshInterval("300")
                .build();

        // When
        dashboard.setRefreshInterval("600");

        // Then
        assertEquals("600", dashboard.getRefreshInterval());
    }

    @Test
    @DisplayName("Should handle chart IDs")
    void shouldHandleChartIds() {
        // Given
        List<UUID> chartIds = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .chartIds(chartIds)
                .build();

        // Then
        assertNotNull(dashboard.getChartIds());
        assertEquals(3, dashboard.getChartIds().size());
    }

    @Test
    @DisplayName("Should handle dashboard metadata")
    void shouldHandleDashboardMetadata() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .build();

        Map<String, Object> filters = new HashMap<>();
        filters.put("dateRange", "last7days");
        filters.put("tags", List.of("sales", "revenue"));

        // When
        dashboard.setFilters(filters);

        // Then
        assertEquals(filters, dashboard.getFilters());
        assertEquals(2, dashboard.getFilters().size());
        assertTrue(filters.get("tags") instanceof List);
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .filters(null)
                .chartIds(null)
                .tags(null)
                .metadata(null)
                .build();

        // Then
        assertNotNull(dashboard);
        assertNull(dashboard.getFilters());
        assertNull(dashboard.getChartIds());
        assertNull(dashboard.getTags());
        assertNull(dashboard.getMetadata());
    }

    @Test
    @DisplayName("Should verify equality and hashCode")
    void shouldVerifyEqualityAndHashCode() {
        // Given
        Dashboard dashboard1 = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .name("Test Dashboard")
                .build();

        Dashboard dashboard2 = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .name("Test Dashboard")
                .build();

        Dashboard dashboard3 = Dashboard.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name("Different Dashboard")
                .build();

        // Then
        assertEquals(dashboard1, dashboard2);
        assertEquals(dashboard1.hashCode(), dashboard2.hashCode());
        assertNotEquals(dashboard1, dashboard3);
        assertNotEquals(dashboard1, null);
        assertNotEquals(dashboard1, new Object());
    }

    @Test
    @DisplayName("Should handle dashboard tags")
    void shouldHandleDashboardTags() {
        // Given
        List<String> tags = List.of("analytics", "sales", "performance");

        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .tags(tags)
                .build();

        // Then
        assertNotNull(dashboard.getTags());
        assertEquals(3, dashboard.getTags().size());
        assertTrue(dashboard.getTags().contains("analytics"));
    }

    @Test
    @DisplayName("Should handle dashboard metadata field")
    void shouldHandleDashboardMetadataField() {
        // Given
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("viewCount", 150);
        metadata.put("lastAccessedBy", "user@example.com");

        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .metadata(metadata)
                .build();

        // Then
        assertNotNull(dashboard.getMetadata());
        assertEquals(2, dashboard.getMetadata().size());
        assertEquals(150, dashboard.getMetadata().get("viewCount"));
    }

    @Test
    @DisplayName("Should handle auto refresh flag")
    void shouldHandleAutoRefreshFlag() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .autoRefresh(true)
                .refreshInterval("300")
                .build();

        // Then
        assertTrue(dashboard.getAutoRefresh());
        assertEquals("300", dashboard.getRefreshInterval());

        // When
        dashboard.setAutoRefresh(false);

        // Then
        assertFalse(dashboard.getAutoRefresh());
    }

    @Test
    @DisplayName("Should handle dashboard category and display order")
    void shouldHandleCategoryAndDisplayOrder() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .category("Business Intelligence")
                .displayOrder(5)
                .build();

        // Then
        assertEquals("Business Intelligence", dashboard.getCategory());
        assertEquals(5, dashboard.getDisplayOrder());
    }

    @Test
    @DisplayName("Should handle active status")
    void shouldHandleActiveStatus() {
        // Given
        Dashboard dashboard = Dashboard.builder()
                .id(testId)
                .tenantId(tenantId)
                .isActive(true)
                .build();

        // Then
        assertTrue(dashboard.getIsActive());

        // When
        dashboard.setIsActive(false);

        // Then
        assertFalse(dashboard.getIsActive());
    }
}
