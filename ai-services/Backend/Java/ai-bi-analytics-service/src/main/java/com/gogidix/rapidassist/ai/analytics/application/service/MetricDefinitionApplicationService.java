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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service for Metric Definition operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricDefinitionApplicationService {

    private final MetricDefinitionRepositoryPort metricRepository;
    private final MetricDefinitionMapper metricMapper;
    private final KafkaEventPublisher eventPublisher;

    /**
     * Create a new metric definition
     */
    @Transactional
    public MetricDefinitionDto createMetric(CreateMetricCommand command) {
        log.info("Creating metric: {} for tenant: {}", command.getName(), command.getTenantId());

        // Validate command
        validateCreateMetricCommand(command);

        // Check if code already exists
        if (metricRepository.existsByCodeAndTenantId(command.getCode(), command.getTenantId())) {
            throw new InvalidReportDataException("Metric code already exists: " + command.getCode());
        }

        // Create metric
        MetricDefinition metric = createMetricFromCommand(command);

        // Save metric
        metric = metricRepository.save(metric);

        // Publish event
        eventPublisher.publishMetricCreatedEvent(metric);

        log.info("Metric created successfully: {}", metric.getId());
        return metricMapper.toDto(metric);
    }

    /**
     * Get metric by ID
     */
    @Transactional(readOnly = true)
    public MetricDefinitionDto getMetric(GetMetricQuery query) {
        log.info("Getting metric: {} for tenant: {}", query.getMetricId(), query.getTenantId());

        UUID metricId = UUID.fromString(query.getMetricId());
        MetricDefinition metric = metricRepository.findByIdAndTenantId(metricId, query.getTenantId())
                .orElseThrow(() -> new MetricNotFoundException(metricId));

        return metricMapper.toDto(metric);
    }

    /**
     * Get metric by code
     */
    @Transactional(readOnly = true)
    public MetricDefinitionDto getMetricByCode(String code, String tenantId) {
        log.info("Getting metric by code: {} for tenant: {}", code, tenantId);

        MetricDefinition metric = metricRepository.findByCodeAndTenantId(code, tenantId)
                .orElseThrow(() -> new MetricNotFoundException("Metric not found with code: " + code));

        return metricMapper.toDto(metric);
    }

    /**
     * List all metrics for a tenant
     */
    @Transactional(readOnly = true)
    public List<MetricDefinitionDto> listMetrics(String tenantId, Boolean isActive, String category) {
        log.info("Listing metrics for tenant: {} with filters - isActive: {}, category: {}", tenantId, isActive, category);

        List<MetricDefinition> metrics;

        if (isActive != null && category != null) {
            metrics = metricRepository.findByTenantIdAndIsActive(tenantId, isActive).stream()
                    .filter(m -> category.equals(m.getCategory()))
                    .toList();
        } else if (isActive != null) {
            metrics = metricRepository.findByTenantIdAndIsActive(tenantId, isActive);
        } else if (category != null) {
            metrics = metricRepository.findByTenantIdAndCategory(tenantId, category);
        } else {
            metrics = metricRepository.findByTenantId(tenantId);
        }

        return metricMapper.toDtoList(metrics);
    }

    /**
     * Delete metric by ID
     */
    @Transactional
    public void deleteMetric(String metricId, String tenantId) {
        log.info("Deleting metric: {} for tenant: {}", metricId, tenantId);

        UUID id = UUID.fromString(metricId);
        if (!metricRepository.existsById(id)) {
            throw new MetricNotFoundException(id);
        }

        metricRepository.deleteById(id);
        log.info("Metric deleted successfully: {}", metricId);
    }

    private void validateCreateMetricCommand(CreateMetricCommand command) {
        if (command.getTenantId() == null || command.getTenantId().isBlank()) {
            throw new InvalidReportDataException("Tenant ID is required");
        }
        if (command.getName() == null || command.getName().isBlank()) {
            throw new InvalidReportDataException("Metric name is required");
        }
        if (command.getCode() == null || command.getCode().isBlank()) {
            throw new InvalidReportDataException("Metric code is required");
        }
        if (command.getMetricType() == null || command.getMetricType().isBlank()) {
            throw new InvalidReportDataException("Metric type is required");
        }
        try {
            MetricType.valueOf(command.getMetricType());
        } catch (IllegalArgumentException e) {
            throw new InvalidReportDataException("Invalid metric type: " + command.getMetricType());
        }
    }

    private MetricDefinition createMetricFromCommand(CreateMetricCommand command) {
        return MetricDefinition.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .name(command.getName())
                .code(command.getCode())
                .description(command.getDescription())
                .metricType(MetricType.valueOf(command.getMetricType()))
                .dataSource(command.getDataSource())
                .query(command.getQuery())
                .configuration(command.getConfiguration())
                .unit(command.getUnit())
                .aggregationFunction(command.getAggregationFunction())
                .createdBy(command.getCreatedBy())
                .updatedBy(command.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .tags(command.getTags())
                .isActive(command.getIsActive() != null ? command.getIsActive() : Boolean.TRUE)
                .category(command.getCategory())
                .thresholdWarning(command.getThresholdWarning())
                .thresholdCritical(command.getThresholdCritical())
                .formatPattern(command.getFormatPattern())
                .build();
    }
}
