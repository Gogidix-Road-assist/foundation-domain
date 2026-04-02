package com.gogidix.rapidassist.ai.analytics.domain.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.AnalyticsReport;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.AnalyticsReportEntity;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.mapper.AnalyticsReportPersistenceMapper;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository.AnalyticsReportRepositoryImpl;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository.SpringDataAnalyticsReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnalyticsReportRepositoryImpl
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsReportRepositoryPortTest {

    @Mock
    private SpringDataAnalyticsReportRepository springDataRepository;

    @Mock
    private AnalyticsReportPersistenceMapper persistenceMapper;

    @InjectMocks
    private AnalyticsReportRepositoryImpl repository;

    private AnalyticsReport testReport;
    private AnalyticsReportEntity testEntity;

    @BeforeEach
    void setUp() {
        testReport = AnalyticsReport.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .name("Test Report")
                .reportType(com.gogidix.rapidassist.ai.analytics.domain.model.ReportType.SUMMARY)
                .status(com.gogidix.rapidassist.ai.analytics.domain.model.ReportStatus.PENDING)
                .createdBy("user-123")
                .createdAt(LocalDateTime.now())
                .build();

        testEntity = AnalyticsReportEntity.builder()
                .id(testReport.getId().toString())
                .tenantId(testReport.getTenantId())
                .name(testReport.getName())
                .reportType(testReport.getReportType().name())
                .status(testReport.getStatus().name())
                .createdBy(testReport.getCreatedBy())
                .build();
    }

    @Test
    void save_WithValidReport_ShouldReturnSavedReport() {
        // Given
        when(persistenceMapper.toEntity(testReport)).thenReturn(testEntity);
        when(springDataRepository.save(any(AnalyticsReportEntity.class))).thenReturn(testEntity);
        when(persistenceMapper.toDomain(testEntity)).thenReturn(testReport);

        // When
        AnalyticsReport result = repository.save(testReport);

        // Then
        assertNotNull(result);
        assertEquals("Test Report", result.getName());
        verify(springDataRepository, times(1)).save(any(AnalyticsReportEntity.class));
    }

    @Test
    void findById_WithValidId_ShouldReturnOptionalOfReport() {
        // Given
        when(springDataRepository.findById(testReport.getId().toString()))
                .thenReturn(Optional.of(testEntity));
        when(persistenceMapper.toDomain(testEntity)).thenReturn(testReport);

        // When
        Optional<AnalyticsReport> result = repository.findById(testReport.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Report", result.get().getName());
        verify(springDataRepository, times(1)).findById(testReport.getId().toString());
    }

    @Test
    void findById_WithInvalidId_ShouldReturnEmptyOptional() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(springDataRepository.findById(invalidId.toString()))
                .thenReturn(Optional.empty());

        // When
        Optional<AnalyticsReport> result = repository.findById(invalidId);

        // Then
        assertFalse(result.isPresent());
        verify(springDataRepository, times(1)).findById(invalidId.toString());
    }

    @Test
    void findByIdAndTenantId_WithValidIds_ShouldReturnOptionalOfReport() {
        // Given
        when(springDataRepository.findById(testReport.getId().toString()))
                .thenReturn(Optional.of(testEntity));
        when(persistenceMapper.toDomain(testEntity)).thenReturn(testReport);

        // When
        Optional<AnalyticsReport> result = repository.findByIdAndTenantId(
                testReport.getId(),
                testReport.getTenantId()
        );

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Report", result.get().getName());
        verify(springDataRepository, times(1)).findById(testReport.getId().toString());
    }

    @Test
    void findByTenantId_WithValidTenantId_ShouldReturnListOfReports() {
        // Given
        when(springDataRepository.findByTenantId("tenant-123"))
                .thenReturn(List.of(testEntity));
        when(persistenceMapper.toDomainList(any(List.class)))
                .thenReturn(List.of(testReport));

        // When
        List<AnalyticsReport> result = repository.findByTenantId("tenant-123");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Report", result.get(0).getName());
        verify(springDataRepository, times(1)).findByTenantId("tenant-123");
    }

    @Test
    void deleteById_WithValidId_ShouldDeleteSuccessfully() {
        // Given
        doNothing().when(springDataRepository).deleteById(testReport.getId().toString());

        // When
        repository.deleteById(testReport.getId());

        // Then
        verify(springDataRepository, times(1)).deleteById(testReport.getId().toString());
    }

    @Test
    void existsById_WithValidId_ShouldReturnTrue() {
        // Given
        when(springDataRepository.existsById(testReport.getId().toString())).thenReturn(true);

        // When
        boolean result = repository.existsById(testReport.getId());

        // Then
        assertTrue(result);
        verify(springDataRepository, times(1)).existsById(testReport.getId().toString());
    }

    @Test
    void existsById_WithInvalidId_ShouldReturnFalse() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(springDataRepository.existsById(invalidId.toString())).thenReturn(false);

        // When
        boolean result = repository.existsById(invalidId);

        // Then
        assertFalse(result);
        verify(springDataRepository, times(1)).existsById(invalidId.toString());
    }
}
