package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity.SummaryConfigEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for SummaryConfigEntity.
 */
@Repository
public interface SpringDataSummaryConfigRepository extends MongoRepository<SummaryConfigEntity, String> {

    Optional<SummaryConfigEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<SummaryConfigEntity> findByTenantId(String tenantId);

    List<SummaryConfigEntity> findByTenantIdAndIsActive(String tenantId, boolean isActive);

    Optional<SummaryConfigEntity> findByTenantIdAndConfigNameAndIsActive(String tenantId, String configName, boolean isActive);

    Optional<SummaryConfigEntity> findFirstByTenantIdOrderByCreatedAtAsc(String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);
}
