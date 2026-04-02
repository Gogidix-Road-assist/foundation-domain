package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity.ImageAnalysisEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ImageAnalysis
 */
@Repository
public interface ImageAnalysisMongoRepository extends MongoRepository<ImageAnalysisEntity, String> {

    // All queries MUST include tenant filtering for multi-tenancy
    Optional<ImageAnalysisEntity> findByTenantIdAndUuid(String tenantId, UUID uuid);

    List<ImageAnalysisEntity> findByTenantId(String tenantId);

    List<ImageAnalysisEntity> findByTenantIdAndUserId(String tenantId, String userId);

    List<ImageAnalysisEntity> findByTenantIdAndStatus(String tenantId, String status);

    List<ImageAnalysisEntity> findByTenantIdAndAnalysisType(String tenantId, String analysisType);

    @Query("{'tenantId': ?0, 'status': ?1}")
    List<ImageAnalysisEntity> findByTenantIdAndStatusWithPaging(String tenantId, String status,
                                                                 org.springframework.data.domain.Pageable pageable);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);

    long countByTenantId(String tenantId);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);
}
