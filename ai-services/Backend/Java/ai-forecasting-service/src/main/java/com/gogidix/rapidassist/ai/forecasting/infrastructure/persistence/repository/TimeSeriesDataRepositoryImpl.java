package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.TimeSeriesData;
import com.gogidix.rapidassist.ai.forecasting.domain.repository.TimeSeriesDataRepositoryPort;
import com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity.TimeSeriesDataEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of TimeSeriesDataRepositoryPort.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TimeSeriesDataRepositoryImpl implements TimeSeriesDataRepositoryPort {

    private final SpringDataTimeSeriesDataRepository springDataRepository;

    @Override
    public TimeSeriesData save(String tenantId, TimeSeriesData timeSeriesData) {
        log.info("Saving time series data: {} for tenant: {}", timeSeriesData.getId(), tenantId);
        TimeSeriesDataEntity entity = toEntity(timeSeriesData);
        entity.setTenantId(tenantId);
        TimeSeriesDataEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TimeSeriesData> findById(String tenantId, UUID dataId) {
        return springDataRepository.findByUuidAndTenantId(dataId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<TimeSeriesData> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<TimeSeriesData> findByDataSourceName(String tenantId, String dataSourceName) {
        return springDataRepository.findByDataSourceNameAndTenantId(dataSourceName, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<TimeSeriesData> findByGranularity(String tenantId, DataGranularity granularity) {
        return springDataRepository.findByGranularityAndTenantId(granularity, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<TimeSeriesData> findWithSeasonality(String tenantId) {
        return springDataRepository.findByHasSeasonalityAndTenantId(true, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<TimeSeriesData> findWithTrend(String tenantId) {
        return springDataRepository.findByHasTrendAndTenantId(true, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(String tenantId, UUID dataId) {
        springDataRepository.deleteByUuidAndTenantId(dataId, tenantId);
    }

    @Override
    public boolean exists(String tenantId, UUID dataId) {
        return springDataRepository.existsByUuidAndTenantId(dataId, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    private TimeSeriesData toDomain(TimeSeriesDataEntity entity) {
        return TimeSeriesData.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .dataSourceName(entity.getDataSourceName())
                .description(entity.getDescription())
                .granularity(entity.getGranularity())
                .frequencyType(entity.getFrequencyType())
                .hasSeasonality(entity.getHasSeasonality())
                .hasTrend(entity.getHasTrend())
                .seasonalityPeriod(entity.getSeasonalityPeriod())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .totalDataPoints(entity.getTotalDataPoints())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private TimeSeriesDataEntity toEntity(TimeSeriesData domain) {
        return TimeSeriesDataEntity.builder()
                .uuid(domain.getId() != null ? domain.getId() : UUID.randomUUID())
                .tenantId(domain.getTenantId())
                .dataSourceName(domain.getDataSourceName())
                .description(domain.getDescription())
                .granularity(domain.getGranularity())
                .frequencyType(domain.getFrequencyType())
                .hasSeasonality(domain.getHasSeasonality())
                .hasTrend(domain.getHasTrend())
                .seasonalityPeriod(domain.getSeasonalityPeriod())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .totalDataPoints(domain.getTotalDataPoints())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
