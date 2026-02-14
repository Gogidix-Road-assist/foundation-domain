package com.gogidix.rapidassist.analytics.domain.port.out;

import com.gogidix.rapidassist.analytics.domain.model.Metric;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Metric operations.
 * Following hexagonal architecture principles.
 */
public interface MetricRepositoryPort {

    Metric save(String tenantId, Metric metric);

    Optional<Metric> findById(String tenantId, UUID id);

    List<Metric> findByTenantId(String tenantId);

    List<Metric> findByTenantIdAndMetricName(String tenantId, String metricName);

    List<Metric> findByTenantIdAndMetricCategory(String tenantId, String metricCategory);

    List<Metric> findByTenantIdAndTimeRange(String tenantId, LocalDateTime startTime, LocalDateTime endTime);

    List<Metric> findByTenantIdAndMetricNameAndTimeRange(
            String tenantId, String metricName, LocalDateTime startTime, LocalDateTime endTime);

    List<Metric> findByTenantIdAndSource(String tenantId, String source);

    List<Metric> findByTenantIdAndStatus(String tenantId, String status);

    boolean exists(String tenantId, UUID id);

    void delete(String tenantId, UUID id);

    void deleteByMetricName(String tenantId, String metricName);

    List<Metric> saveAll(String tenantId, List<Metric> metrics);
}
