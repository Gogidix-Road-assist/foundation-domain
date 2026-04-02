package com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.entity.SearchResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for SearchResultEntity.
 */
@Repository
public interface SpringDataSearchResultRepository extends MongoRepository<SearchResultEntity, String> {

    Optional<SearchResultEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<SearchResultEntity> findByQueryId(UUID queryId);

    List<SearchResultEntity> findByQueryIdAndTenantId(UUID queryId, String tenantId);

    List<SearchResultEntity> findByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByQueryId(UUID queryId);

    long countByQueryId(UUID queryId);
}
