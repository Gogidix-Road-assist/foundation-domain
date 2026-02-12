package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.SummaryQualityMetricsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataSummaryQualityMetricsRepository extends MongoRepository<SummaryQualityMetricsEntity, String> {

    Optional<SummaryQualityMetricsEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<SummaryQualityMetricsEntity> findBySummaryIdAndTenantId(UUID summaryId, String tenantId);

    List<SummaryQualityMetricsEntity> findByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
