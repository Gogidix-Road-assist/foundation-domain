package com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.entity.CategorizationResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCategorizationResultRepository extends MongoRepository<CategorizationResultEntity, String> {

    List<CategorizationResultEntity> findByTenantId(String tenantId);

    Optional<CategorizationResultEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<CategorizationResultEntity> findByRequestIdAndTenantId(UUID requestId, String tenantId);

    List<CategorizationResultEntity> findByContentIdAndTenantId(String contentId, String tenantId);

    List<CategorizationResultEntity> findByTenantIdAndStatus(String tenantId, String status);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
