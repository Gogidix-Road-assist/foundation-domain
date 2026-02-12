package com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.entity.ForecastEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ForecastEntity.
 */
@Repository
public interface SpringDataForecastRepository extends MongoRepository<ForecastEntity, String> {

    /**
     * Find forecast by UUID and tenant.
     */
    Optional<ForecastEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find forecasts by model ID and tenant.
     */
    List<ForecastEntity> findByModelIdAndTenantId(UUID modelId, String tenantId);

    /**
     * Find forecasts by generation date range and tenant.
     */
    List<ForecastEntity> findByGeneratedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Find forecasts by forecast start date range and tenant.
     */
    List<ForecastEntity> findByForecastStartDateBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Find all forecasts for a tenant.
     */
    List<ForecastEntity> findByTenantId(String tenantId);

    /**
     * Count forecasts by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count forecasts by model and tenant.
     */
    long countByModelIdAndTenantId(UUID modelId, String tenantId);

    /**
     * Delete old forecasts.
     */
    @Query("{ 'generatedAt': { $lt: ?0 } }")
    void deleteByGeneratedAtBefore(LocalDateTime cutoffDate);

    /**
     * Delete forecast by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
