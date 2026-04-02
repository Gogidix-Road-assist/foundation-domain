package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity.ForecastModelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ForecastModelEntity.
 */
@Repository
public interface SpringDataForecastModelRepository extends MongoRepository<ForecastModelEntity, Long> {

    List<ForecastModelEntity> findByTenantId(String tenantId);

    Optional<ForecastModelEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<ForecastModelEntity> findByModelTypeAndTenantId(ForecastModelType modelType, String tenantId);

    List<ForecastModelEntity> findByStatusAndTenantId(ModelStatus status, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);
}
