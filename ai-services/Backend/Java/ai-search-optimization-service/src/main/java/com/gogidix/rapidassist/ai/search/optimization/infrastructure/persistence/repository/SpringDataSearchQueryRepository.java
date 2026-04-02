package com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.entity.SearchQueryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for SearchQueryEntity.
 */
@Repository
public interface SpringDataSearchQueryRepository extends MongoRepository<SearchQueryEntity, String> {

    Optional<SearchQueryEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<SearchQueryEntity> findByTenantId(String tenantId);

    List<SearchQueryEntity> findByTenantIdAndUserId(String tenantId, String userId);

    List<SearchQueryEntity> findByTenantIdAndStatus(String tenantId, String status);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);
}
