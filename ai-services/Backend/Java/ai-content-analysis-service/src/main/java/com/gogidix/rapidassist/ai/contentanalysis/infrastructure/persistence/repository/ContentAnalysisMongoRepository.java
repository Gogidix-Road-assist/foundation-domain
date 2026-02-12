package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ContentAnalysisEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ContentAnalysisEntity
 */
@Repository
public interface ContentAnalysisMongoRepository extends MongoRepository<ContentAnalysisEntity, UUID> {

    Optional<ContentAnalysisEntity> findByIdAndTenantId(UUID id, String tenantId);
    List<ContentAnalysisEntity> findByTenantId(String tenantId);
    Optional<ContentAnalysisEntity> findByContentId(String contentId);
    Optional<ContentAnalysisEntity> findByContentIdAndTenantId(String contentId, String tenantId);
    List<ContentAnalysisEntity> findByStatus(String status);
    List<ContentAnalysisEntity> findByStatusAndTenantId(String status, String tenantId);
    boolean existsByIdAndTenantId(UUID id, String tenantId);
    void deleteByIdAndTenantId(UUID id, String tenantId);
}
