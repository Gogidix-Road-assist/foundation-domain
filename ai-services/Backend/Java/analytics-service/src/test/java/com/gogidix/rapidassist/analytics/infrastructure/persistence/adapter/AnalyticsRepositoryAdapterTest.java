package com.gogidix.rapidassist.analytics.infrastructure.persistence.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.analytics.domain.model.Analytics;
import com.gogidix.rapidassist.analytics.infrastructure.persistence.entity.AnalyticsEntity;
import com.gogidix.rapidassist.analytics.infrastructure.persistence.mongo.AnalyticsMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for AnalyticsRepositoryAdapter.
 * Tests all CRUD operations and mapping between domain and entity.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Analytics Repository Adapter Tests")
class AnalyticsRepositoryAdapterTest {

    @Mock
    private AnalyticsMongoRepository mongoRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AnalyticsRepositoryAdapter repositoryAdapter;

    private Analytics testDomain;
    private AnalyticsEntity testEntity;
    private String tenantId;
    private UUID analyticsId;

    @BeforeEach
    void setUp() throws Exception {
        tenantId = "tenant-123";
        analyticsId = UUID.randomUUID();

        Map<String, Object> metrics = Map.of("views", 1000, "clicks", 500);
        Map<String, Object> dimensions = Map.of("region", "US");
        Map<String, Object> metadata = Map.of("version", "1.0");

        String metricsJson = "{\"views\":1000,\"clicks\":500}";
        String dimensionsJson = "{\"region\":\"US\"}";
        String metadataJson = "{\"version\":\"1.0\"}";

        testDomain = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .dataSource("DATABASE")
                .metrics(metrics)
                .dimensions(dimensions)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .status("PENDING")
                .totalRecords(1500)
                .aggregationValue(1000.0)
                .aggregationType("SUM")
                .computedBy("system")
                .description("Test analytics")
                .metadata(metadata)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testEntity = AnalyticsEntity.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .dataSource("DATABASE")
                .metrics(metricsJson)
                .dimensions(dimensionsJson)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .status("PENDING")
                .totalRecords(1500)
                .aggregationValue(1000.0)
                .aggregationType("SUM")
                .computedBy("system")
                .description("Test analytics")
                .metadata(metadataJson)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Setup ObjectMapper mock behavior
        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn(metricsJson);
        when(objectMapper.readValue(eq(metricsJson), eq(Map.class))).thenReturn(metrics);
        when(objectMapper.readValue(eq(dimensionsJson), eq(Map.class))).thenReturn(dimensions);
        when(objectMapper.readValue(eq(metadataJson), eq(Map.class))).thenReturn(metadata);
    }

    @Test
    @DisplayName("Should save analytics successfully")
    void shouldSaveAnalyticsSuccessfully() throws Exception {
        // Given
        when(mongoRepository.save(any(AnalyticsEntity.class))).thenReturn(testEntity);
        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn("{\"key\":\"value\"}");

        // When
        Analytics result = repositoryAdapter.save(tenantId, testDomain);

        // Then
        assertNotNull(result);
        assertEquals(analyticsId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals("USER_ENGAGEMENT", result.getAnalyticsType());

        verify(mongoRepository, times(1)).save(any(AnalyticsEntity.class));
    }

    @Test
    @DisplayName("Should find analytics by id successfully")
    void shouldFindAnalyticsByIdSuccessfully() throws Exception {
        // Given
        when(mongoRepository.findById(analyticsId)).thenReturn(Optional.of(testEntity));

        // When
        Optional<Analytics> result = repositoryAdapter.findById(tenantId, analyticsId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(analyticsId, result.get().getId());
        assertEquals(tenantId, result.get().getTenantId());

        verify(mongoRepository, times(1)).findById(analyticsId);
    }

    @Test
    @DisplayName("Should return empty when analytics id not found")
    void shouldReturnEmptyWhenAnalyticsIdNotFound() {
        // Given
        when(mongoRepository.findById(analyticsId)).thenReturn(Optional.empty());

        // When
        Optional<Analytics> result = repositoryAdapter.findById(tenantId, analyticsId);

        // Then
        assertFalse(result.isPresent());
        verify(mongoRepository, times(1)).findById(analyticsId);
    }

    @Test
    @DisplayName("Should return empty when tenant id does not match")
    void shouldReturnEmptyWhenTenantIdDoesNotMatch() {
        // Given
        AnalyticsEntity differentTenantEntity = AnalyticsEntity.builder()
                .id(analyticsId)
                .tenantId("different-tenant")
                .build();
        when(mongoRepository.findById(analyticsId)).thenReturn(Optional.of(differentTenantEntity));

        // When
        Optional<Analytics> result = repositoryAdapter.findById(tenantId, analyticsId);

        // Then
        assertFalse(result.isPresent());
        verify(mongoRepository, times(1)).findById(analyticsId);
    }

    @Test
    @DisplayName("Should find all analytics by tenant id")
    void shouldFindAllAnalyticsByTenantId() throws Exception {
        // Given
        List<AnalyticsEntity> entities = List.of(testEntity);
        when(mongoRepository.findByTenantId(tenantId)).thenReturn(entities);

        // When
        List<Analytics> result = repositoryAdapter.findByTenantId(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(analyticsId, result.get(0).getId());

        verify(mongoRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should return empty list when no analytics found for tenant")
    void shouldReturnEmptyListWhenNoAnalyticsFound() {
        // Given
        when(mongoRepository.findByTenantId(tenantId)).thenReturn(List.of());

        // When
        List<Analytics> result = repositoryAdapter.findByTenantId(tenantId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should find analytics by tenant id and status")
    void shouldFindAnalyticsByTenantIdAndStatus() throws Exception {
        // Given
        String status = "PENDING";
        List<AnalyticsEntity> entities = List.of(testEntity);
        when(mongoRepository.findByTenantIdAndStatus(tenantId, status)).thenReturn(entities);

        // When
        List<Analytics> result = repositoryAdapter.findByTenantIdAndStatus(tenantId, status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());

        verify(mongoRepository, times(1)).findByTenantIdAndStatus(tenantId, status);
    }

    @Test
    @DisplayName("Should find analytics by tenant id and analytics type")
    void shouldFindAnalyticsByTenantIdAndAnalyticsType() throws Exception {
        // Given
        String analyticsType = "USER_ENGAGEMENT";
        List<AnalyticsEntity> entities = List.of(testEntity);
        when(mongoRepository.findByTenantIdAndAnalyticsType(tenantId, analyticsType)).thenReturn(entities);

        // When
        List<Analytics> result = repositoryAdapter.findByTenantIdAndAnalyticsType(tenantId, analyticsType);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(analyticsType, result.get(0).getAnalyticsType());

        verify(mongoRepository, times(1)).findByTenantIdAndAnalyticsType(tenantId, analyticsType);
    }

    @Test
    @DisplayName("Should find analytics by tenant id and data source")
    void shouldFindAnalyticsByTenantIdAndDataSource() throws Exception {
        // Given
        String dataSource = "DATABASE";
        List<AnalyticsEntity> entities = List.of(testEntity);
        when(mongoRepository.findByTenantIdAndDataSource(tenantId, dataSource)).thenReturn(entities);

        // When
        List<Analytics> result = repositoryAdapter.findByTenantIdAndDataSource(tenantId, dataSource);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dataSource, result.get(0).getDataSource());

        verify(mongoRepository, times(1)).findByTenantIdAndDataSource(tenantId, dataSource);
    }

    @Test
    @DisplayName("Should find analytics by tenant id and time range")
    void shouldFindAnalyticsByTenantIdAndTimeRange() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        List<AnalyticsEntity> entities = List.of(testEntity);
        when(mongoRepository.findByTenantIdAndStartTimeBetween(tenantId, startTime, endTime)).thenReturn(entities);

        // When
        List<Analytics> result = repositoryAdapter.findByTenantIdAndTimeRange(tenantId, startTime, endTime);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(mongoRepository, times(1)).findByTenantIdAndStartTimeBetween(tenantId, startTime, endTime);
    }

    @Test
    @DisplayName("Should check if analytics exists")
    void shouldCheckIfAnalyticsExists() {
        // Given
        when(mongoRepository.existsByTenantIdAndId(tenantId, analyticsId)).thenReturn(true);

        // When
        boolean exists = repositoryAdapter.exists(tenantId, analyticsId);

        // Then
        assertTrue(exists);
        verify(mongoRepository, times(1)).existsByTenantIdAndId(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should return false when analytics does not exist")
    void shouldReturnFalseWhenAnalyticsDoesNotExist() {
        // Given
        when(mongoRepository.existsByTenantIdAndId(tenantId, analyticsId)).thenReturn(false);

        // When
        boolean exists = repositoryAdapter.exists(tenantId, analyticsId);

        // Then
        assertFalse(exists);
        verify(mongoRepository, times(1)).existsByTenantIdAndId(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should delete analytics successfully")
    void shouldDeleteAnalyticsSuccessfully() {
        // Given
        doNothing().when(mongoRepository).deleteByTenantIdAndId(tenantId, analyticsId);

        // When
        repositoryAdapter.delete(tenantId, analyticsId);

        // Then
        verify(mongoRepository, times(1)).deleteByTenantIdAndId(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should find pending computations before timestamp")
    void shouldFindPendingComputationsBeforeTimestamp() throws Exception {
        // Given
        LocalDateTime before = LocalDateTime.now();
        List<AnalyticsEntity> entities = List.of(testEntity);
        when(mongoRepository.findByTenantIdAndStatusAndComputedAtBefore(tenantId, "PENDING", before))
                .thenReturn(entities);

        // When
        List<Analytics> result = repositoryAdapter.findPendingComputations(tenantId, before);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());

        verify(mongoRepository, times(1)).findByTenantIdAndStatusAndComputedAtBefore(tenantId, "PENDING", before);
    }

    @Test
    @DisplayName("Should handle null metrics in entity conversion")
    void shouldHandleNullMetricsInEntityConversion() throws Exception {
        // Given
        Analytics domainWithNullMetrics = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .metrics(null)
                .build();

        when(mongoRepository.save(any(AnalyticsEntity.class))).thenReturn(testEntity);
        when(objectMapper.writeValueAsString(null)).thenReturn(null);

        // When
        repositoryAdapter.save(tenantId, domainWithNullMetrics);

        // Then
        verify(mongoRepository, times(1)).save(any(AnalyticsEntity.class));
    }

    @Test
    @DisplayName("Should handle null dimensions in entity conversion")
    void shouldHandleNullDimensionsInEntityConversion() throws Exception {
        // Given
        Analytics domainWithNullDimensions = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .dimensions(null)
                .build();

        when(mongoRepository.save(any(AnalyticsEntity.class))).thenReturn(testEntity);
        when(objectMapper.writeValueAsString(null)).thenReturn(null);

        // When
        repositoryAdapter.save(tenantId, domainWithNullDimensions);

        // Then
        verify(mongoRepository, times(1)).save(any(AnalyticsEntity.class));
    }

    @Test
    @DisplayName("Should handle null metadata in entity conversion")
    void shouldHandleNullMetadataInEntityConversion() throws Exception {
        // Given
        Analytics domainWithNullMetadata = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .metadata(null)
                .build();

        when(mongoRepository.save(any(AnalyticsEntity.class))).thenReturn(testEntity);
        when(objectMapper.writeValueAsString(null)).thenReturn(null);

        // When
        repositoryAdapter.save(tenantId, domainWithNullMetadata);

        // Then
        verify(mongoRepository, times(1)).save(any(AnalyticsEntity.class));
    }

    @Test
    @DisplayName("Should handle multiple analytics in list")
    void shouldHandleMultipleAnalyticsInList() throws Exception {
        // Given
        AnalyticsEntity entity1 = AnalyticsEntity.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_1")
                .build();
        AnalyticsEntity entity2 = AnalyticsEntity.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_2")
                .build();
        AnalyticsEntity entity3 = AnalyticsEntity.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_3")
                .build();

        List<AnalyticsEntity> entities = List.of(entity1, entity2, entity3);
        when(mongoRepository.findByTenantId(tenantId)).thenReturn(entities);

        // When
        List<Analytics> result = repositoryAdapter.findByTenantId(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());

        verify(mongoRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should throw exception when entity conversion fails")
    void shouldThrowExceptionWhenEntityConversionFails() throws Exception {
        // Given
        when(objectMapper.writeValueAsString(any(Map.class))).thenThrow(new RuntimeException("JSON conversion error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> repositoryAdapter.save(tenantId, testDomain));
    }

    @Test
    @DisplayName("Should handle empty results from repository")
    void shouldHandleEmptyResultsFromRepository() {
        // Given
        when(mongoRepository.findByTenantIdAndStatus(tenantId, "COMPLETED")).thenReturn(List.of());

        // When
        List<Analytics> result = repositoryAdapter.findByTenantIdAndStatus(tenantId, "COMPLETED");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByTenantIdAndStatus(tenantId, "COMPLETED");
    }

    @Test
    @DisplayName("Should properly set tenant id in save operation")
    void shouldProperlySetTenantIdInSaveOperation() throws Exception {
        // Given
        Analytics domainWithoutTenant = Analytics.builder()
                .id(analyticsId)
                .analyticsType("USER_ENGAGEMENT")
                .build();

        when(mongoRepository.save(any(AnalyticsEntity.class))).thenReturn(testEntity);

        // When
        repositoryAdapter.save(tenantId, domainWithoutTenant);

        // Then
        verify(mongoRepository, times(1)).save(argThat(entity -> tenantId.equals(entity.getTenantId())));
    }

    @Test
    @DisplayName("Should find analytics with different statuses")
    void shouldFindAnalyticsWithDifferentStatuses() throws Exception {
        // Given
        String pendingStatus = "PENDING";
        String completedStatus = "COMPLETED";
        String failedStatus = "FAILED";

        AnalyticsEntity pendingEntity = AnalyticsEntity.builder().status(pendingStatus).build();
        AnalyticsEntity completedEntity = AnalyticsEntity.builder().status(completedStatus).build();
        AnalyticsEntity failedEntity = AnalyticsEntity.builder().status(failedStatus).build();

        when(mongoRepository.findByTenantIdAndStatus(tenantId, pendingStatus)).thenReturn(List.of(pendingEntity));
        when(mongoRepository.findByTenantIdAndStatus(tenantId, completedStatus)).thenReturn(List.of(completedEntity));
        when(mongoRepository.findByTenantIdAndStatus(tenantId, failedStatus)).thenReturn(List.of(failedEntity));

        // When
        List<Analytics> pendingResults = repositoryAdapter.findByTenantIdAndStatus(tenantId, pendingStatus);
        List<Analytics> completedResults = repositoryAdapter.findByTenantIdAndStatus(tenantId, completedStatus);
        List<Analytics> failedResults = repositoryAdapter.findByTenantIdAndStatus(tenantId, failedStatus);

        // Then
        assertNotNull(pendingResults);
        assertNotNull(completedResults);
        assertNotNull(failedResults);
        assertEquals(1, pendingResults.size());
        assertEquals(1, completedResults.size());
        assertEquals(1, failedResults.size());

        verify(mongoRepository, times(1)).findByTenantIdAndStatus(tenantId, pendingStatus);
        verify(mongoRepository, times(1)).findByTenantIdAndStatus(tenantId, completedStatus);
        verify(mongoRepository, times(1)).findByTenantIdAndStatus(tenantId, failedStatus);
    }
}
