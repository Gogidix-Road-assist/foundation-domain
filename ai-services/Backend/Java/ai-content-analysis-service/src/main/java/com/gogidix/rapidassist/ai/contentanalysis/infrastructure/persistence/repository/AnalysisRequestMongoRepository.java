package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.AnalysisRequestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for AnalysisRequestEntity
 */
@Repository
public interface AnalysisRequestMongoRepository extends MongoRepository<AnalysisRequestEntity, UUID> {

    Optional<AnalysisRequestEntity> findByIdAndTenantId(UUID id, String tenantId);
    Optional<AnalysisRequestEntity> findByRequestId(String requestId);
    Optional<AnalysisRequestEntity> findByRequestIdAndTenantId(String requestId, String tenantId);
    List<AnalysisRequestEntity> findByContentId(String contentId);
    List<AnalysisRequestEntity> findByContentIdAndTenantId(String contentId, String tenantId);
    List<AnalysisRequestEntity> findByStatus(String status);
    List<AnalysisRequestEntity> findByStatusAndTenantId(String status, String tenantId);
    List<AnalysisRequestEntity> findByStatusOrderByPriorityDescCreatedAtAsc(String status);
    List<AnalysisRequestEntity> findByStatusAndPriorityGreaterThanEqual(String status, Integer minPriority);
    void deleteByIdAndTenantId(UUID id, String tenantId);
}
