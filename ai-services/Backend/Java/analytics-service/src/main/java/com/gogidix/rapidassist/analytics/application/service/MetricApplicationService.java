package com.gogidix.rapidassist.analytics.application.service;

import com.gogidix.rapidassist.analytics.application.dto.MetricDto;
import com.gogidix.rapidassist.analytics.application.mapper.MetricMapper;
import com.gogidix.rapidassist.analytics.domain.model.Metric;
import com.gogidix.rapidassist.analytics.domain.port.out.MetricRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Metric operations.
 * Implements business logic and orchestrates domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricApplicationService {

    private final MetricRepositoryPort metricRepository;
    private final MetricMapper mapper;

    public MetricDto createMetric(String tenantId, MetricDto dto) {
        log.info("Creating metric for tenant: {}", tenantId);

        var metric = Metric.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .metricName(dto.getMetricName())
                .metricCategory(dto.getMetricCategory())
                .metricValue(dto.getMetricValue())
                .metricUnit(dto.getMetricUnit())
                .metricType(dto.getMetricType())
                .dimensions(dto.getDimensions())
                .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                .source(dto.getSource())
                .granularity(dto.getGranularity())
                .tags(dto.getTags())
                .threshold(dto.getThreshold())
                .status("ACTIVE")
                .description(dto.getDescription())
                .metadata(dto.getMetadata())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var saved = metricRepository.save(tenantId, metric);
        return mapper.toDto(saved);
    }

    public MetricDto getMetric(String tenantId, UUID id) {
        log.info("Getting metric: {} for tenant: {}", id, tenantId);

        var metric = metricRepository.findById(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Metric not found: " + id));

        return mapper.toDto(metric);
    }

    public List<MetricDto> getMetricsByTenant(String tenantId) {
        log.info("Getting all metrics for tenant: {}", tenantId);

        var metrics = metricRepository.findByTenantId(tenantId);
        return mapper.toDtoList(metrics);
    }

    public List<MetricDto> getMetricsByName(String tenantId, String metricName) {
        log.info("Getting metrics by name: {} for tenant: {}", metricName, tenantId);

        var metrics = metricRepository.findByTenantIdAndMetricName(tenantId, metricName);
        return mapper.toDtoList(metrics);
    }

    public List<MetricDto> getMetricsByCategory(String tenantId, String category) {
        log.info("Getting metrics by category: {} for tenant: {}", category, tenantId);

        var metrics = metricRepository.findByTenantIdAndMetricCategory(tenantId, category);
        return mapper.toDtoList(metrics);
    }

    public List<MetricDto> getMetricsByTimeRange(String tenantId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Getting metrics by time range for tenant: {}", tenantId);

        var metrics = metricRepository.findByTenantIdAndTimeRange(tenantId, startTime, endTime);
        return mapper.toDtoList(metrics);
    }

    public List<MetricDto> batchCreateMetrics(String tenantId, List<MetricDto> dtos) {
        log.info("Batch creating {} metrics for tenant: {}", dtos.size(), tenantId);

        var metrics = dtos.stream()
                .map(dto -> Metric.builder()
                        .id(UUID.randomUUID())
                        .tenantId(tenantId)
                        .metricName(dto.getMetricName())
                        .metricCategory(dto.getMetricCategory())
                        .metricValue(dto.getMetricValue())
                        .metricUnit(dto.getMetricUnit())
                        .metricType(dto.getMetricType())
                        .dimensions(dto.getDimensions())
                        .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                        .source(dto.getSource())
                        .granularity(dto.getGranularity())
                        .tags(dto.getTags())
                        .threshold(dto.getThreshold())
                        .status("ACTIVE")
                        .description(dto.getDescription())
                        .metadata(dto.getMetadata())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build())
                .toList();

        var saved = metricRepository.saveAll(tenantId, metrics);
        return mapper.toDtoList(saved);
    }

    public void deleteMetric(String tenantId, UUID id) {
        log.info("Deleting metric: {} for tenant: {}", id, tenantId);

        if (!metricRepository.exists(tenantId, id)) {
            throw new IllegalArgumentException("Metric not found: " + id);
        }

        metricRepository.delete(tenantId, id);
    }
}
