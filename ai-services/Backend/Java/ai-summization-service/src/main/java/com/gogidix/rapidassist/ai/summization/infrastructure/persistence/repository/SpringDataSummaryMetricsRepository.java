package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity.SummaryMetricsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for SummaryMetricsEntity.
 */
@Repository
public interface SpringDataSummaryMetricsRepository extends MongoRepository<SummaryMetricsEntity, String> {

    Optional<SummaryMetricsEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<SummaryMetricsEntity> findByDocumentSummaryIdAndTenantId(UUID summaryId, String tenantId);

    List<SummaryMetricsEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);
}
