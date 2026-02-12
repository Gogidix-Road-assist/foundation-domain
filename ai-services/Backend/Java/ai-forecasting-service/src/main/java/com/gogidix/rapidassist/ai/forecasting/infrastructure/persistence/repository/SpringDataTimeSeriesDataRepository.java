package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity.TimeSeriesDataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for TimeSeriesDataEntity.
 */
@Repository
public interface SpringDataTimeSeriesDataRepository extends MongoRepository<TimeSeriesDataEntity, Long> {

    List<TimeSeriesDataEntity> findByTenantId(String tenantId);

    Optional<TimeSeriesDataEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<TimeSeriesDataEntity> findByDataSourceNameAndTenantId(String dataSourceName, String tenantId);

    List<TimeSeriesDataEntity> findByGranularityAndTenantId(DataGranularity granularity, String tenantId);

    List<TimeSeriesDataEntity> findByHasSeasonalityAndTenantId(Boolean hasSeasonality, String tenantId);

    List<TimeSeriesDataEntity> findByHasTrendAndTenantId(Boolean hasTrend, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);
}
