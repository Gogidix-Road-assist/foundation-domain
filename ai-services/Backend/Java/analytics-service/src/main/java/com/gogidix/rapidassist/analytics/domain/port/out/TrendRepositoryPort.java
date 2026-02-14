package com.gogidix.rapidassist.analytics.domain.port.out;

import com.gogidix.rapidassist.analytics.domain.model.Trend;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Trend operations.
 * Following hexagonal architecture principles.
 */
public interface TrendRepositoryPort {

    Trend save(String tenantId, Trend trend);

    Optional<Trend> findById(String tenantId, UUID id);

    List<Trend> findByTenantId(String tenantId);

    List<Trend> findByTenantIdAndMetricName(String tenantId, String metricName);

    List<Trend> findByTenantIdAndTrendType(String tenantId, String trendType);

    List<Trend> findByTenantIdAndTimeRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    List<Trend> findByTenantIdAndStatus(String tenantId, String status);

    boolean exists(String tenantId, UUID id);

    void delete(String tenantId, UUID id);
}
