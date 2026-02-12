package com.gogidix.rapidassist.analytics.application.service;

import com.gogidix.rapidassist.analytics.application.dto.AnalyticsDto;
import com.gogidix.rapidassist.analytics.application.mapper.AnalyticsMapper;
import com.gogidix.rapidassist.analytics.domain.model.Analytics;
import com.gogidix.rapidassist.analytics.domain.port.out.AnalyticsRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for AnalyticsApplicationService.
 * Tests all use cases with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Analytics Application Service Tests")
class AnalyticsApplicationServiceTest {

    @Mock
    private AnalyticsRepositoryPort analyticsRepository;

    @Mock
    private AnalyticsMapper mapper;

    @InjectMocks
    private AnalyticsApplicationService applicationService;

    private AnalyticsDto testDto;
    private Analytics testDomain;
    private String tenantId;
    private UUID analyticsId;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-123";
        analyticsId = UUID.randomUUID();

        testDto = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .dataSource("DATABASE")
                .metrics(Map.of("views", 1000, "clicks", 500))
                .dimensions(Map.of("region", "US"))
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .status("PENDING")
                .totalRecords(1500)
                .aggregationType("SUM")
                .computedBy("system")
                .description("Test analytics")
                .metadata(Map.of("version", "1.0"))
                .build();

        testDomain = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .dataSource("DATABASE")
                .metrics(Map.of("views", 1000, "clicks", 500))
                .dimensions(Map.of("region", "US"))
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .status("PENDING")
                .totalRecords(1500)
                .aggregationType("SUM")
                .computedBy("system")
                .description("Test analytics")
                .metadata(Map.of("version", "1.0"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create analytics successfully")
    void shouldCreateAnalyticsSuccessfully() {
        // Given
        when(analyticsRepository.save(eq(tenantId), any(Analytics.class))).thenReturn(testDomain);
        when(mapper.toDto(any(Analytics.class))).thenReturn(testDto);

        // When
        AnalyticsDto result = applicationService.createAnalytics(tenantId, testDto);

        // Then
        assertNotNull(result);
        assertEquals(analyticsId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals("USER_ENGAGEMENT", result.getAnalyticsType());

        verify(analyticsRepository, times(1)).save(eq(tenantId), any(Analytics.class));
        verify(mapper, times(1)).toDto(any(Analytics.class));
    }

    @Test
    @DisplayName("Should get analytics by id successfully")
    void shouldGetAnalyticsByIdSuccessfully() {
        // Given
        when(analyticsRepository.findById(tenantId, analyticsId)).thenReturn(Optional.of(testDomain));
        when(mapper.toDto(testDomain)).thenReturn(testDto);

        // When
        AnalyticsDto result = applicationService.getAnalytics(tenantId, analyticsId);

        // Then
        assertNotNull(result);
        assertEquals(analyticsId, result.getId());
        assertEquals(tenantId, result.getTenantId());

        verify(analyticsRepository, times(1)).findById(tenantId, analyticsId);
        verify(mapper, times(1)).toDto(testDomain);
    }

    @Test
    @DisplayName("Should throw exception when analytics not found")
    void shouldThrowExceptionWhenAnalyticsNotFound() {
        // Given
        when(analyticsRepository.findById(tenantId, analyticsId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.getAnalytics(tenantId, analyticsId)
        );

        assertEquals("Analytics not found: " + analyticsId, exception.getMessage());
        verify(analyticsRepository, times(1)).findById(tenantId, analyticsId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should get all analytics by tenant")
    void shouldGetAllAnalyticsByTenant() {
        // Given
        List<Analytics> analyticsList = List.of(testDomain);
        List<AnalyticsDto> dtoList = List.of(testDto);

        when(analyticsRepository.findByTenantId(tenantId)).thenReturn(analyticsList);
        when(mapper.toDtoList(analyticsList)).thenReturn(dtoList);

        // When
        List<AnalyticsDto> result = applicationService.getAnalyticsByTenant(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(analyticsId, result.get(0).getId());

        verify(analyticsRepository, times(1)).findByTenantId(tenantId);
        verify(mapper, times(1)).toDtoList(analyticsList);
    }

    @Test
    @DisplayName("Should return empty list when no analytics found for tenant")
    void shouldReturnEmptyListWhenNoAnalyticsFound() {
        // Given
        when(analyticsRepository.findByTenantId(tenantId)).thenReturn(List.of());
        when(mapper.toDtoList(anyList())).thenReturn(List.of());

        // When
        List<AnalyticsDto> result = applicationService.getAnalyticsByTenant(tenantId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(analyticsRepository, times(1)).findByTenantId(tenantId);
        verify(mapper, times(1)).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should get analytics by status")
    void shouldGetAnalyticsByStatus() {
        // Given
        String status = "PENDING";
        List<Analytics> analyticsList = List.of(testDomain);
        List<AnalyticsDto> dtoList = List.of(testDto);

        when(analyticsRepository.findByTenantIdAndStatus(tenantId, status)).thenReturn(analyticsList);
        when(mapper.toDtoList(analyticsList)).thenReturn(dtoList);

        // When
        List<AnalyticsDto> result = applicationService.getAnalyticsByStatus(tenantId, status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());

        verify(analyticsRepository, times(1)).findByTenantIdAndStatus(tenantId, status);
        verify(mapper, times(1)).toDtoList(analyticsList);
    }

    @Test
    @DisplayName("Should get analytics by type")
    void shouldGetAnalyticsByType() {
        // Given
        String analyticsType = "USER_ENGAGEMENT";
        List<Analytics> analyticsList = List.of(testDomain);
        List<AnalyticsDto> dtoList = List.of(testDto);

        when(analyticsRepository.findByTenantIdAndAnalyticsType(tenantId, analyticsType)).thenReturn(analyticsList);
        when(mapper.toDtoList(analyticsList)).thenReturn(dtoList);

        // When
        List<AnalyticsDto> result = applicationService.getAnalyticsByType(tenantId, analyticsType);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USER_ENGAGEMENT", result.get(0).getAnalyticsType());

        verify(analyticsRepository, times(1)).findByTenantIdAndAnalyticsType(tenantId, analyticsType);
        verify(mapper, times(1)).toDtoList(analyticsList);
    }

    @Test
    @DisplayName("Should get analytics by time range")
    void shouldGetAnalyticsByTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        List<Analytics> analyticsList = List.of(testDomain);
        List<AnalyticsDto> dtoList = List.of(testDto);

        when(analyticsRepository.findByTenantIdAndTimeRange(tenantId, startTime, endTime)).thenReturn(analyticsList);
        when(mapper.toDtoList(analyticsList)).thenReturn(dtoList);

        // When
        List<AnalyticsDto> result = applicationService.getAnalyticsByTimeRange(tenantId, startTime, endTime);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(analyticsRepository, times(1)).findByTenantIdAndTimeRange(tenantId, startTime, endTime);
        verify(mapper, times(1)).toDtoList(analyticsList);
    }

    @Test
    @DisplayName("Should compute analytics successfully")
    void shouldComputeAnalyticsSuccessfully() {
        // Given
        Analytics readyForComputation = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .status("PENDING")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .build();

        Analytics computed = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .status("COMPLETED")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .computedAt(LocalDateTime.now())
                .aggregationValue(1000.0)
                .build();

        AnalyticsDto computedDto = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .status("COMPLETED")
                .aggregationValue(1000.0)
                .build();

        when(analyticsRepository.findById(tenantId, analyticsId)).thenReturn(Optional.of(readyForComputation));
        when(analyticsRepository.save(eq(tenantId), any(Analytics.class))).thenReturn(computed);
        when(mapper.toDto(computed)).thenReturn(computedDto);

        // When
        AnalyticsDto result = applicationService.computeAnalytics(tenantId, analyticsId);

        // Then
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        assertEquals(1000.0, result.getAggregationValue());
        assertNotNull(result.getComputedAt());

        verify(analyticsRepository, times(1)).findById(tenantId, analyticsId);
        verify(analyticsRepository, times(1)).save(eq(tenantId), any(Analytics.class));
        verify(mapper, times(1)).toDto(computed);
    }

    @Test
    @DisplayName("Should throw exception when computing analytics that is not ready")
    void shouldThrowExceptionWhenComputingAnalyticsNotReady() {
        // Given
        Analytics notReadyAnalytics = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .status("PROCESSING")
                .startTime(null)
                .endTime(null)
                .build();

        when(analyticsRepository.findById(tenantId, analyticsId)).thenReturn(Optional.of(notReadyAnalytics));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> applicationService.computeAnalytics(tenantId, analyticsId)
        );

        assertEquals("Analytics is not ready for computation", exception.getMessage());
        verify(analyticsRepository, times(1)).findById(tenantId, analyticsId);
        verify(analyticsRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when computing non-existent analytics")
    void shouldThrowExceptionWhenComputingNonExistentAnalytics() {
        // Given
        when(analyticsRepository.findById(tenantId, analyticsId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.computeAnalytics(tenantId, analyticsId)
        );

        assertEquals("Analytics not found: " + analyticsId, exception.getMessage());
        verify(analyticsRepository, times(1)).findById(tenantId, analyticsId);
        verify(analyticsRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should delete analytics successfully")
    void shouldDeleteAnalyticsSuccessfully() {
        // Given
        when(analyticsRepository.exists(tenantId, analyticsId)).thenReturn(true);
        doNothing().when(analyticsRepository).delete(tenantId, analyticsId);

        // When
        applicationService.deleteAnalytics(tenantId, analyticsId);

        // Then
        verify(analyticsRepository, times(1)).exists(tenantId, analyticsId);
        verify(analyticsRepository, times(1)).delete(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent analytics")
    void shouldThrowExceptionWhenDeletingNonExistentAnalytics() {
        // Given
        when(analyticsRepository.exists(tenantId, analyticsId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.deleteAnalytics(tenantId, analyticsId)
        );

        assertEquals("Analytics not found: " + analyticsId, exception.getMessage());
        verify(analyticsRepository, times(1)).exists(tenantId, analyticsId);
        verify(analyticsRepository, never()).delete(any(), any());
    }

    @Test
    @DisplayName("Should handle analytics with different statuses")
    void shouldHandleAnalyticsWithDifferentStatuses() {
        // Given
        String pendingStatus = "PENDING";
        String completedStatus = "COMPLETED";
        String failedStatus = "FAILED";

        Analytics pendingAnalytics = Analytics.builder().status(pendingStatus).build();
        Analytics completedAnalytics = Analytics.builder().status(completedStatus).build();
        Analytics failedAnalytics = Analytics.builder().status(failedStatus).build();

        when(analyticsRepository.findByTenantIdAndStatus(tenantId, pendingStatus))
                .thenReturn(List.of(pendingAnalytics));
        when(analyticsRepository.findByTenantIdAndStatus(tenantId, completedStatus))
                .thenReturn(List.of(completedAnalytics));
        when(analyticsRepository.findByTenantIdAndStatus(tenantId, failedStatus))
                .thenReturn(List.of(failedAnalytics));

        when(mapper.toDtoList(anyList())).thenReturn(List.of());

        // When
        List<AnalyticsDto> pendingResults = applicationService.getAnalyticsByStatus(tenantId, pendingStatus);
        List<AnalyticsDto> completedResults = applicationService.getAnalyticsByStatus(tenantId, completedStatus);
        List<AnalyticsDto> failedResults = applicationService.getAnalyticsByStatus(tenantId, failedStatus);

        // Then
        assertNotNull(pendingResults);
        assertNotNull(completedResults);
        assertNotNull(failedResults);

        verify(analyticsRepository, times(1)).findByTenantIdAndStatus(tenantId, pendingStatus);
        verify(analyticsRepository, times(1)).findByTenantIdAndStatus(tenantId, completedStatus);
        verify(analyticsRepository, times(1)).findByTenantIdAndStatus(tenantId, failedStatus);
    }

    @Test
    @DisplayName("Should handle analytics with different types")
    void shouldHandleAnalyticsWithDifferentTypes() {
        // Given
        String engagementType = "USER_ENGAGEMENT";
        String salesType = "SALES_ANALYTICS";
        String trafficType = "TRAFFIC_ANALYTICS";

        when(analyticsRepository.findByTenantIdAndAnalyticsType(tenantId, engagementType))
                .thenReturn(List.of(testDomain));
        when(analyticsRepository.findByTenantIdAndAnalyticsType(tenantId, salesType))
                .thenReturn(List.of());
        when(analyticsRepository.findByTenantIdAndAnalyticsType(tenantId, trafficType))
                .thenReturn(List.of());

        when(mapper.toDtoList(anyList())).thenReturn(List.of());

        // When
        List<AnalyticsDto> engagementResults = applicationService.getAnalyticsByType(tenantId, engagementType);
        List<AnalyticsDto> salesResults = applicationService.getAnalyticsByType(tenantId, salesType);
        List<AnalyticsDto> trafficResults = applicationService.getAnalyticsByType(tenantId, trafficType);

        // Then
        assertNotNull(engagementResults);
        assertNotNull(salesResults);
        assertNotNull(trafficResults);

        verify(analyticsRepository, times(1)).findByTenantIdAndAnalyticsType(tenantId, engagementType);
        verify(analyticsRepository, times(1)).findByTenantIdAndAnalyticsType(tenantId, salesType);
        verify(analyticsRepository, times(1)).findByTenantIdAndAnalyticsType(tenantId, trafficType);
    }

    @Test
    @DisplayName("Should handle multiple analytics in list")
    void shouldHandleMultipleAnalyticsInList() {
        // Given
        Analytics analytics1 = Analytics.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_1")
                .build();
        Analytics analytics2 = Analytics.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_2")
                .build();
        Analytics analytics3 = Analytics.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_3")
                .build();

        List<Analytics> analyticsList = List.of(analytics1, analytics2, analytics3);

        when(analyticsRepository.findByTenantId(tenantId)).thenReturn(analyticsList);
        when(mapper.toDtoList(analyticsList)).thenReturn(List.of(testDto, testDto, testDto));

        // When
        List<AnalyticsDto> result = applicationService.getAnalyticsByTenant(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());

        verify(analyticsRepository, times(1)).findByTenantId(tenantId);
        verify(mapper, times(1)).toDtoList(analyticsList);
    }

    @Test
    @DisplayName("Should handle analytics with null metadata")
    void shouldHandleAnalyticsWithNullMetadata() {
        // Given
        AnalyticsDto dtoWithNullMetadata = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .metadata(null)
                .build();

        Analytics domainWithNullMetadata = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .metadata(null)
                .build();

        when(analyticsRepository.save(eq(tenantId), any(Analytics.class))).thenReturn(domainWithNullMetadata);
        when(mapper.toDto(any(Analytics.class))).thenReturn(dtoWithNullMetadata);

        // When
        AnalyticsDto result = applicationService.createAnalytics(tenantId, dtoWithNullMetadata);

        // Then
        assertNotNull(result);
        assertNull(result.getMetadata());

        verify(analyticsRepository, times(1)).save(eq(tenantId), any(Analytics.class));
    }

    @Test
    @DisplayName("Should handle analytics with empty metrics and dimensions")
    void shouldHandleAnalyticsWithEmptyMetricsAndDimensions() {
        // Given
        AnalyticsDto dtoWithEmptyMaps = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .metrics(Map.of())
                .dimensions(Map.of())
                .build();

        when(analyticsRepository.save(eq(tenantId), any(Analytics.class))).thenReturn(testDomain);
        when(mapper.toDto(any(Analytics.class))).thenReturn(dtoWithEmptyMaps);

        // When
        AnalyticsDto result = applicationService.createAnalytics(tenantId, dtoWithEmptyMaps);

        // Then
        assertNotNull(result);
        assertNotNull(result.getMetrics());
        assertNotNull(result.getDimensions());
        assertTrue(result.getMetrics().isEmpty());
        assertTrue(result.getDimensions().isEmpty());

        verify(analyticsRepository, times(1)).save(eq(tenantId), any(Analytics.class));
    }
}
