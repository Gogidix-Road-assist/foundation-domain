package com.gogidix.rapidassist.ai.analytics.application.service;

import com.gogidix.rapidassist.ai.analytics.application.command.CreateMetricCommand;
import com.gogidix.rapidassist.ai.analytics.application.dto.MetricDefinitionDto;
import com.gogidix.rapidassist.ai.analytics.application.mapper.MetricDefinitionMapper;
import com.gogidix.rapidassist.ai.analytics.application.query.GetMetricQuery;
import com.gogidix.rapidassist.ai.analytics.domain.exception.InvalidReportDataException;
import com.gogidix.rapidassist.ai.analytics.domain.exception.MetricNotFoundException;
import com.gogidix.rapidassist.ai.analytics.domain.model.MetricDefinition;
import com.gogidix.rapidassist.ai.analytics.domain.model.MetricType;
import com.gogidix.rapidassist.ai.analytics.domain.repository.MetricDefinitionRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.messaging.kafka.event.KafkaEventPublisher;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for MetricDefinitionApplicationService
 */
@ExtendWith(MockitoExtension.class)
class MetricDefinitionApplicationServiceTest {

    @Mock
    private MetricDefinitionRepositoryPort metricRepository;

    @Mock
    private MetricDefinitionMapper metricMapper;

    @Mock
    private KafkaEventPublisher eventPublisher;

    @InjectMocks
    private MetricDefinitionApplicationService metricService;

    private CreateMetricCommand validCommand;
    private MetricDefinition testMetric;
    private MetricDefinitionDto testMetricDto;

    @BeforeEach
    void setUp() {
        validCommand = CreateMetricCommand.builder()
                .tenantId("tenant-123")
                .name("Test Metric")
                .code("TEST_METRIC")
                .description("Test Description")
                .metricType("COUNTER")
                .createdBy("user-123")
                .build();

        testMetric = MetricDefinition.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .name("Test Metric")
                .code("TEST_METRIC")
                .description("Test Description")
                .metricType(MetricType.COUNTER)
                .createdBy("user-123")
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();

        testMetricDto = MetricDefinitionDto.builder()
                .id(testMetric.getId().toString())
                .tenantId(testMetric.getTenantId())
                .name(testMetric.getName())
                .code(testMetric.getCode())
                .description(testMetric.getDescription())
                .metricType(testMetric.getMetricType().name())
                .createdBy(testMetric.getCreatedBy())
                .build();
    }

    @Test
    void createMetric_WithValidCommand_ShouldReturnMetricDto() {
        // Given
        when(metricRepository.existsByCodeAndTenantId("TEST_METRIC", "tenant-123")).thenReturn(false);
        when(metricRepository.save(any(MetricDefinition.class))).thenReturn(testMetric);
        when(metricMapper.toDto(any(MetricDefinition.class))).thenReturn(testMetricDto);

        // When
        MetricDefinitionDto result = metricService.createMetric(validCommand);

        // Then
        assertNotNull(result);
        assertEquals("Test Metric", result.getName());
        assertEquals("TEST_METRIC", result.getCode());
        assertEquals("COUNTER", result.getMetricType());
        verify(metricRepository, times(1)).save(any(MetricDefinition.class));
        verify(eventPublisher, times(1)).publishMetricCreatedEvent(any(MetricDefinition.class));
    }

    @Test
    void createMetric_WithDuplicateCode_ShouldThrowException() {
        // Given
        when(metricRepository.existsByCodeAndTenantId("TEST_METRIC", "tenant-123")).thenReturn(true);

        // When/Then
        assertThrows(InvalidReportDataException.class, () -> metricService.createMetric(validCommand));
        verify(metricRepository, never()).save(any(MetricDefinition.class));
    }

    @Test
    void createMetric_WithMissingTenantId_ShouldThrowException() {
        // Given
        CreateMetricCommand invalidCommand = CreateMetricCommand.builder()
                .name("Test Metric")
                .code("TEST_METRIC")
                .metricType("COUNTER")
                .build();

        // When/Then
        assertThrows(InvalidReportDataException.class, () -> metricService.createMetric(invalidCommand));
        verify(metricRepository, never()).save(any(MetricDefinition.class));
    }

    @Test
    void createMetric_WithMissingMetricName_ShouldThrowException() {
        // Given
        CreateMetricCommand invalidCommand = CreateMetricCommand.builder()
                .tenantId("tenant-123")
                .code("TEST_METRIC")
                .metricType("COUNTER")
                .build();

        // When/Then
        assertThrows(InvalidReportDataException.class, () -> metricService.createMetric(invalidCommand));
        verify(metricRepository, never()).save(any(MetricDefinition.class));
    }

    @Test
    void getMetric_WithValidId_ShouldReturnMetricDto() {
        // Given
        GetMetricQuery query = GetMetricQuery.builder()
                .metricId(testMetric.getId().toString())
                .tenantId("tenant-123")
                .build();

        when(metricRepository.findByIdAndTenantId(testMetric.getId(), "tenant-123"))
                .thenReturn(Optional.of(testMetric));
        when(metricMapper.toDto(testMetric)).thenReturn(testMetricDto);

        // When
        MetricDefinitionDto result = metricService.getMetric(query);

        // Then
        assertNotNull(result);
        assertEquals("Test Metric", result.getName());
        verify(metricRepository, times(1)).findByIdAndTenantId(testMetric.getId(), "tenant-123");
    }

    @Test
    void getMetric_WithInvalidId_ShouldThrowException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        GetMetricQuery query = GetMetricQuery.builder()
                .metricId(invalidId.toString())
                .tenantId("tenant-123")
                .build();

        when(metricRepository.findByIdAndTenantId(invalidId, "tenant-123"))
                .thenReturn(Optional.empty());

        // When/Then
        assertThrows(MetricNotFoundException.class, () -> metricService.getMetric(query));
    }

    @Test
    void listMetrics_WithValidTenantId_ShouldReturnListOfMetrics() {
        // Given
        when(metricRepository.findByTenantId("tenant-123"))
                .thenReturn(List.of(testMetric));
        when(metricMapper.toDtoList(any(List.class)))
                .thenReturn(List.of(testMetricDto));

        // When
        List<MetricDefinitionDto> result = metricService.listMetrics("tenant-123", null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Metric", result.get(0).getName());
        verify(metricRepository, times(1)).findByTenantId("tenant-123");
    }

    @Test
    void deleteMetric_WithValidId_ShouldDeleteSuccessfully() {
        // Given
        when(metricRepository.existsById(testMetric.getId())).thenReturn(true);
        doNothing().when(metricRepository).deleteById(testMetric.getId());

        // When
        metricService.deleteMetric(testMetric.getId().toString(), "tenant-123");

        // Then
        verify(metricRepository, times(1)).deleteById(testMetric.getId());
    }

    @Test
    void deleteMetric_WithInvalidId_ShouldThrowException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(metricRepository.existsById(invalidId)).thenReturn(false);

        // When/Then
        assertThrows(MetricNotFoundException.class,
                () -> metricService.deleteMetric(invalidId.toString(), "tenant-123"));
        verify(metricRepository, never()).deleteById(any(UUID.class));
    }
}
