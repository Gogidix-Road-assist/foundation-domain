package com.gogidix.rapidassist.ai.dataquality.domain.repository;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityMetric;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for DataQualityMetric aggregate
 */
public interface DataQualityMetricRepositoryPort {

    DataQualityMetric save(DataQualityMetric metric);

    Optional<DataQualityMetric> findById(String tenantId, UUID id);

    List<DataQualityMetric> findByTenantId(String tenantId);

    List<DataQualityMetric> findByTenantIdAndMetricName(String tenantId, String metricName);

    List<DataQualityMetric> findByTenantIdAndEntityType(String tenantId, String entityType);

    List<DataQualityMetric> findByTenantIdAndMetricTimestampBetween(
            String tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<DataQualityMetric> findLatestMetricsByTenantIdAndMetricName(
            String tenantId,
            String metricName,
            int limit
    );

    Optional<DataQualityMetric> findLatestByTenantIdAndMetricNameAndEntityType(
            String tenantId,
            String metricName,
            String entityType
    );

    void deleteById(String tenantId, UUID id);

    List<DataQualityMetric> findRecentMetricsByTenantId(String tenantId, int limit);

    long countByTenantId(String tenantId);
}
