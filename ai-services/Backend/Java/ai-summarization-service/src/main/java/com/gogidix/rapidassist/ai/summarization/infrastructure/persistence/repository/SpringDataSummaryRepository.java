package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.SummaryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataSummaryRepository extends MongoRepository<SummaryEntity, String> {

    Optional<SummaryEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<SummaryEntity> findBySummarizationRequestIdAndTenantId(UUID requestId, String tenantId);

    List<SummaryEntity> findByTenantId(String tenantId);

    List<SummaryEntity> findByCreatedByAndTenantId(String createdBy, String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
