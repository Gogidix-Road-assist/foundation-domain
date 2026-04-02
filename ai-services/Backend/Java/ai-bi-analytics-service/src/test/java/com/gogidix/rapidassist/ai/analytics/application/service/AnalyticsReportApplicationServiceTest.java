package com.gogidix.rapidassist.ai.analytics.application.service;

import com.gogidix.rapidassist.ai.analytics.application.command.GenerateReportCommand;
import com.gogidix.rapidassist.ai.analytics.application.dto.AnalyticsReportDto;
import com.gogidix.rapidassist.ai.analytics.application.mapper.AnalyticsReportMapper;
import com.gogidix.rapidassist.ai.analytics.application.query.GetReportQuery;
import com.gogidix.rapidassist.ai.analytics.application.query.ListReportsQuery;
import com.gogidix.rapidassist.ai.analytics.domain.exception.AnalyticsReportNotFoundException;
import com.gogidix.rapidassist.ai.analytics.domain.exception.InvalidReportDataException;
import com.gogidix.rapidassist.ai.analytics.domain.model.AnalyticsReport;
import com.gogidix.rapidassist.ai.analytics.domain.model.ReportStatus;
import com.gogidix.rapidassist.ai.analytics.domain.model.ReportType;
import com.gogidix.rapidassist.ai.analytics.domain.repository.AnalyticsReportRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.messaging.kafka.event.KafkaEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnalyticsReportApplicationService
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsReportApplicationServiceTest {

    @Mock
    private AnalyticsReportRepositoryPort reportRepository;

    @Mock
    private AnalyticsReportMapper reportMapper;

    @Mock
    private KafkaEventPublisher eventPublisher;

    @InjectMocks
    private AnalyticsReportApplicationService reportService;

    private GenerateReportCommand validCommand;
    private AnalyticsReport testReport;
    private AnalyticsReportDto testReportDto;

    @BeforeEach
    void setUp() {
        validCommand = GenerateReportCommand.builder()
                .tenantId("tenant-123")
                .name("Test Report")
                .description("Test Description")
                .reportType("SUMMARY")
                .triggeredBy("user-123")
                .build();

        testReport = AnalyticsReport.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .name("Test Report")
                .description("Test Description")
                .reportType(ReportType.SUMMARY)
                .status(ReportStatus.COMPLETED)
                .createdBy("user-123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .executionTimeMs(100L)
                .recordCount(10)
                .generationCount(1)
                .isActive(true)
                .build();

        testReportDto = AnalyticsReportDto.builder()
                .id(testReport.getId().toString())
                .tenantId(testReport.getTenantId())
                .name(testReport.getName())
                .description(testReport.getDescription())
                .reportType(testReport.getReportType().name())
                .status(testReport.getStatus().name())
                .createdBy(testReport.getCreatedBy())
                .build();
    }

    @Test
    void generateReport_WithValidCommand_ShouldReturnReportDto() {
        // Given
        when(reportRepository.save(any(AnalyticsReport.class))).thenReturn(testReport);
        when(reportMapper.toDto(any(AnalyticsReport.class))).thenReturn(testReportDto);

        // When
        AnalyticsReportDto result = reportService.generateReport(validCommand);

        // Then
        assertNotNull(result);
        assertEquals("Test Report", result.getName());
        assertEquals("SUMMARY", result.getReportType());
        verify(reportRepository, times(2)).save(any(AnalyticsReport.class));
        verify(eventPublisher, times(1)).publishReportRequestedEvent(any(AnalyticsReport.class));
        verify(eventPublisher, times(1)).publishReportGeneratedEvent(any(AnalyticsReport.class));
    }

    @Test
    void generateReport_WithMissingTenantId_ShouldThrowException() {
        // Given
        GenerateReportCommand invalidCommand = GenerateReportCommand.builder()
                .name("Test Report")
                .reportType("SUMMARY")
                .build();

        // When/Then
        assertThrows(InvalidReportDataException.class, () -> reportService.generateReport(invalidCommand));
        verify(reportRepository, never()).save(any(AnalyticsReport.class));
    }

    @Test
    void generateReport_WithMissingReportName_ShouldThrowException() {
        // Given
        GenerateReportCommand invalidCommand = GenerateReportCommand.builder()
                .tenantId("tenant-123")
                .reportType("SUMMARY")
                .build();

        // When/Then
        assertThrows(InvalidReportDataException.class, () -> reportService.generateReport(invalidCommand));
        verify(reportRepository, never()).save(any(AnalyticsReport.class));
    }

    @Test
    void generateReport_WithInvalidReportType_ShouldThrowException() {
        // Given
        GenerateReportCommand invalidCommand = GenerateReportCommand.builder()
                .tenantId("tenant-123")
                .name("Test Report")
                .reportType("INVALID_TYPE")
                .build();

        // When/Then
        assertThrows(InvalidReportDataException.class, () -> reportService.generateReport(invalidCommand));
        verify(reportRepository, never()).save(any(AnalyticsReport.class));
    }

    @Test
    void getReport_WithValidId_ShouldReturnReportDto() {
        // Given
        GetReportQuery query = GetReportQuery.builder()
                .reportId(testReport.getId().toString())
                .tenantId("tenant-123")
                .build();

        when(reportRepository.findByIdAndTenantId(testReport.getId(), "tenant-123"))
                .thenReturn(Optional.of(testReport));
        when(reportMapper.toDto(testReport)).thenReturn(testReportDto);

        // When
        AnalyticsReportDto result = reportService.getReport(query);

        // Then
        assertNotNull(result);
        assertEquals("Test Report", result.getName());
        verify(reportRepository, times(1)).findByIdAndTenantId(testReport.getId(), "tenant-123");
    }

    @Test
    void getReport_WithInvalidId_ShouldThrowException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        GetReportQuery query = GetReportQuery.builder()
                .reportId(invalidId.toString())
                .tenantId("tenant-123")
                .build();

        when(reportRepository.findByIdAndTenantId(invalidId, "tenant-123"))
                .thenReturn(Optional.empty());

        // When/Then
        assertThrows(AnalyticsReportNotFoundException.class, () -> reportService.getReport(query));
    }

    @Test
    void listReports_WithValidQuery_ShouldReturnPageOfReports() {
        // Given
        ListReportsQuery query = ListReportsQuery.builder()
                .tenantId("tenant-123")
                .page(0)
                .size(20)
                .build();

        when(reportRepository.findByTenantId("tenant-123"))
                .thenReturn(List.of(testReport));
        when(reportMapper.toDtoList(any(List.class)))
                .thenReturn(List.of(testReportDto));

        // When
        Page<AnalyticsReportDto> result = reportService.listReports(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Report", result.getContent().get(0).getName());
        verify(reportRepository, times(1)).findByTenantId("tenant-123");
    }

    @Test
    void deleteReport_WithValidId_ShouldDeleteSuccessfully() {
        // Given
        when(reportRepository.existsById(testReport.getId())).thenReturn(true);
        doNothing().when(reportRepository).deleteById(testReport.getId());

        // When
        reportService.deleteReport(testReport.getId().toString(), "tenant-123");

        // Then
        verify(reportRepository, times(1)).deleteById(testReport.getId());
    }

    @Test
    void deleteReport_WithInvalidId_ShouldThrowException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(reportRepository.existsById(invalidId)).thenReturn(false);

        // When/Then
        assertThrows(AnalyticsReportNotFoundException.class,
                () -> reportService.deleteReport(invalidId.toString(), "tenant-123"));
        verify(reportRepository, never()).deleteById(any(UUID.class));
    }
}
