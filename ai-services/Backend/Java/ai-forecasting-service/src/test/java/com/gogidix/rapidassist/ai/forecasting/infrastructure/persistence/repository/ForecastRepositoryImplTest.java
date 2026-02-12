package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.Forecast;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastStatus;
import com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity.ForecastEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ForecastRepositoryImpl.
 */
@ExtendWith(MockitoExtension.class)
class ForecastRepositoryImplTest {

    @Mock
    private SpringDataForecastRepository springDataRepository;

    private ForecastRepositoryImpl forecastRepository;

    @BeforeEach
    void setUp() {
        forecastRepository = new ForecastRepositoryImpl(springDataRepository);
    }

    @Test
    void testSave_Success() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();

        Forecast forecast = Forecast.builder()
                .id(forecastId)
                .tenantId(tenantId)
                .forecastName("Test Forecast")
                .status(ForecastStatus.PENDING)
                .forecastHorizon(12)
                .granularity(DataGranularity.MONTHLY)
                .createdAt(LocalDateTime.now())
                .build();

        ForecastEntity entity = ForecastEntity.builder()
                .uuid(forecastId)
                .tenantId(tenantId)
                .forecastName("Test Forecast")
                .status(ForecastStatus.PENDING)
                .build();

        when(springDataRepository.save(any(ForecastEntity.class))).thenReturn(entity);

        // When
        Forecast result = forecastRepository.save(tenantId, forecast);

        // Then
        assertNotNull(result);
        verify(springDataRepository).save(any(ForecastEntity.class));
    }

    @Test
    void testFindById_Found() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();

        ForecastEntity entity = ForecastEntity.builder()
                .uuid(forecastId)
                .tenantId(tenantId)
                .forecastName("Test Forecast")
                .status(ForecastStatus.COMPLETED)
                .build();

        when(springDataRepository.findByUuidAndTenantId(forecastId, tenantId))
                .thenReturn(Optional.of(entity));

        // When
        Optional<Forecast> result = forecastRepository.findById(tenantId, forecastId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(forecastId, result.get().getId());
    }

    @Test
    void testFindById_NotFound() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();

        when(springDataRepository.findByUuidAndTenantId(forecastId, tenantId))
                .thenReturn(Optional.empty());

        // When
        Optional<Forecast> result = forecastRepository.findById(tenantId, forecastId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testExists_True() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();

        when(springDataRepository.existsByUuidAndTenantId(forecastId, tenantId))
                .thenReturn(true);

        // When
        boolean result = forecastRepository.exists(tenantId, forecastId);

        // Then
        assertTrue(result);
    }

    @Test
    void testDelete_Success() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();

        doNothing().when(springDataRepository).deleteByUuidAndTenantId(forecastId, tenantId);

        // When
        forecastRepository.delete(tenantId, forecastId);

        // Then
        verify(springDataRepository).deleteByUuidAndTenantId(forecastId, tenantId);
    }
}
