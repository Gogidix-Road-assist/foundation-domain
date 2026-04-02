package com.gogidix.rapidassist.ai.search.optimization.application.port.out;

import com.gogidix.rapidassist.ai.search.optimization.domain.model.SearchResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for SearchResult operations.
 */
public interface SearchResultRepositoryPort {

    SearchResult save(SearchResult result);

    Optional<SearchResult> findById(UUID id);

    Optional<SearchResult> findByIdAndTenantId(UUID id, String tenantId);

    List<SearchResult> findByQueryId(UUID queryId);

    List<SearchResult> findByQueryIdAndTenantId(UUID queryId, String tenantId);

    List<SearchResult> findByTenantId(String tenantId);

    void deleteById(UUID id);

    void deleteByIdAndTenantId(UUID id, String tenantId);

    void deleteByQueryId(UUID queryId);

    long countByQueryId(UUID queryId);
}
