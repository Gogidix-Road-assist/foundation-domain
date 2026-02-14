package com.gogidix.rapidassist.ai.search.optimization.application.port.out;

import com.gogidix.rapidassist.ai.search.optimization.domain.model.SearchQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for SearchQuery operations.
 */
public interface SearchQueryRepositoryPort {

    SearchQuery save(SearchQuery query);

    Optional<SearchQuery> findById(UUID id);

    Optional<SearchQuery> findByIdAndTenantId(UUID id, String tenantId);

    List<SearchQuery> findByTenantId(String tenantId);

    List<SearchQuery> findByUserId(String tenantId, String userId);

    List<SearchQuery> findByStatus(String tenantId, String status);

    void deleteById(UUID id);

    void deleteByIdAndTenantId(UUID id, String tenantId);

    boolean existsByIdAndTenantId(UUID id, String tenantId);

    long countByTenantId(String tenantId);
}
