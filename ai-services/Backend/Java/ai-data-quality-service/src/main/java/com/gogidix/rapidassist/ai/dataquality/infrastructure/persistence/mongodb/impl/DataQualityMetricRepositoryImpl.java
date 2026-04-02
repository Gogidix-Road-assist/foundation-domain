package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.impl;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityMetric;
import com.gogidix.rapidassist.ai.dataquality.domain.repository.DataQualityMetricRepositoryPort;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.adapter.DataQualityRepositoryAdapter;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository.SpringDataQualityMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DataQualityMetricRepositoryImpl implements DataQualityMetricRepositoryPort {

    private final SpringDataQualityMetricRepository springRepository;

    @Override
    public DataQualityMetric save(DataQualityMetric metric) {
        var entity = DataQualityRepositoryAdapter.toEntity(metric);
        var saved = springRepository.save(entity);
        return DataQualityRepositoryAdapter.toDomain(saved);
    }

    @Override
    public Optional<DataQualityMetric> findById(String tenantId, UUID id) {
        return springRepository.findByTenantIdAndId(tenantId, id)
                .map(DataQualityRepositoryAdapter::toDomain);
    }

    @Override
    public List<DataQualityMetric> findByTenantId(String tenantId) {
        return springRepository.findByTenantId(tenantId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityMetric> findByTenantIdAndMetricName(String tenantId, String metricName) {
        return springRepository.findByTenantIdAndMetricName(tenantId, metricName).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityMetric> findByTenantIdAndEntityType(String tenantId, String entityType) {
        return springRepository.findByTenantIdAndEntityType(tenantId, entityType).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityMetric> findByTenantIdAndMetricTimestampBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return springRepository.findByTenantIdAndMetricTimestampBetween(tenantId, startDate, endDate).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityMetric> findLatestMetricsByTenantIdAndMetricName(String tenantId, String metricName, int limit) {
        return springRepository.findLatestMetricsByTenantIdAndMetricName(tenantId, metricName)
                .stream()
                .limit(limit)
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DataQualityMetric> findLatestByTenantIdAndMetricNameAndEntityType(String tenantId, String metricName, String entityType) {
        return springRepository.findLatestByTenantIdAndMetricNameAndEntityType(tenantId, metricName, entityType)
                .map(DataQualityRepositoryAdapter::toDomain);
    }

    @Override
    public void deleteById(String tenantId, UUID id) {
        springRepository.findByTenantIdAndId(tenantId, id).ifPresent(entity -> {
            springRepository.delete(entity);
        });
    }

    @Override
    public List<DataQualityMetric> findRecentMetricsByTenantId(String tenantId, int limit) {
        return springRepository.findRecentMetrics(tenantId).stream()
                .limit(limit)
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springRepository.countByTenantId(tenantId);
    }
}
