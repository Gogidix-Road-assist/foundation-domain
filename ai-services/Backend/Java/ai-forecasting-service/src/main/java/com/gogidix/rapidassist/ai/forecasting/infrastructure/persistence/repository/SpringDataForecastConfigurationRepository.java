package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity.ForecastConfigurationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ForecastConfigurationEntity.
 */
@Repository
public interface SpringDataForecastConfigurationRepository extends MongoRepository<ForecastConfigurationEntity, Long> {

    List<ForecastConfigurationEntity> findByTenantId(String tenantId);

    ForecastConfigurationEntity findByUuidAndTenantId(UUID uuid, String tenantId);

    List<ForecastConfigurationEntity> findByIsActiveAndTenantId(Boolean isActive, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);
}
