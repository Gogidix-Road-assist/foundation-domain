package com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.entity.CategorizationRequestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCategorizationRequestRepository extends MongoRepository<CategorizationRequestEntity, String> {

    List<CategorizationRequestEntity> findByTenantId(String tenantId);

    Optional<CategorizationRequestEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<CategorizationRequestEntity> findByContentIdAndTenantId(String contentId, String tenantId);

    List<CategorizationRequestEntity> findByTenantIdAndStatus(String tenantId, String status);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
