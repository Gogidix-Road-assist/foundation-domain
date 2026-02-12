package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.MetricDefinitionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for MetricDefinitionEntity
 */
@Repository
public interface SpringDataMetricDefinitionRepository extends MongoRepository<MetricDefinitionEntity, String> {

    List<MetricDefinitionEntity> findByTenantId(String tenantId);

    List<MetricDefinitionEntity> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    List<MetricDefinitionEntity> findByTenantIdAndCategory(String tenantId, String category);

    Optional<MetricDefinitionEntity> findByCodeAndTenantId(String code, String tenantId);

    boolean existsByCodeAndTenantId(String code, String tenantId);

    boolean existsById(String id);
}
