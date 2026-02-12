package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastStatus;
import com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity.ForecastEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ForecastEntity.
 */
@Repository
public interface SpringDataForecastRepository extends MongoRepository<ForecastEntity, Long> {

    List<ForecastEntity> findByTenantId(String tenantId);

    Optional<ForecastEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<ForecastEntity> findByModelIdAndTenantId(UUID modelId, String tenantId);

    List<ForecastEntity> findByStatusAndTenantId(ForecastStatus status, String tenantId);

    @Query("{'tenantId': ?0, 'startDate': {$gte: ?1}, 'endDate': {$lte: ?2}}")
    List<ForecastEntity> findByTenantIdAndDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);
}
