package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationStatus;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.BatchSummarizationJobEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataBatchSummarizationJobRepository extends MongoRepository<BatchSummarizationJobEntity, String> {

    Optional<BatchSummarizationJobEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<BatchSummarizationJobEntity> findByJobIdAndTenantId(String jobId, String tenantId);

    List<BatchSummarizationJobEntity> findByTenantId(String tenantId);

    List<BatchSummarizationJobEntity> findByStatusAndTenantId(SummarizationStatus status, String tenantId);

    List<BatchSummarizationJobEntity> findByCreatedByAndTenantId(String createdBy, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
