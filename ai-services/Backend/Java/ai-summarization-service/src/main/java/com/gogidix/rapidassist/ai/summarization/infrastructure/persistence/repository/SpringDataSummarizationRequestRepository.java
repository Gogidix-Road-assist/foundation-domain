package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationStatus;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.SummarizationRequestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataSummarizationRequestRepository extends MongoRepository<SummarizationRequestEntity, String> {

    Optional<SummarizationRequestEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<SummarizationRequestEntity> findByRequestIdAndTenantId(String requestId, String tenantId);

    List<SummarizationRequestEntity> findByTenantId(String tenantId);

    List<SummarizationRequestEntity> findByStatusAndTenantId(SummarizationStatus status, String tenantId);

    List<SummarizationRequestEntity> findByCreatedByAndTenantId(String createdBy, String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
