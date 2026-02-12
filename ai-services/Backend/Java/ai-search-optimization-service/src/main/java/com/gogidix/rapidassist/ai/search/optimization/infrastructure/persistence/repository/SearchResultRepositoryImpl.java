package com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.search.optimization.application.port.out.SearchResultRepositoryPort;
import com.gogidix.rapidassist.ai.search.optimization.domain.model.SearchResult;
import com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.entity.SearchResultEntity;
import com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.mapper.SearchResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of SearchResultRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchResultRepositoryImpl implements SearchResultRepositoryPort {

    private final SpringDataSearchResultRepository springDataRepository;
    private final SearchResultMapper mapper;

    @Override
    @Transactional
    public SearchResult save(SearchResult result) {
        log.info("Saving search result: {} for tenant: {}", result.getId(), result.getTenantId());

        SearchResultEntity entity = mapper.toEntity(result);

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());

        SearchResultEntity savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SearchResult> findById(UUID id) {
        log.debug("Finding search result by ID: {}", id);
        // Use findByIdAndTenantId with a default tenant or return empty
        // This method is deprecated in favor of findByIdAndTenantId
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SearchResult> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding search result by ID: {} and tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchResult> findByQueryId(UUID queryId) {
        log.debug("Finding search results for query: {}", queryId);
        return springDataRepository.findByQueryId(queryId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchResult> findByQueryIdAndTenantId(UUID queryId, String tenantId) {
        log.debug("Finding search results for query: {} in tenant: {}", queryId, tenantId);
        return springDataRepository.findByQueryIdAndTenantId(queryId, tenantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchResult> findByTenantId(String tenantId) {
        log.debug("Finding all search results for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting search result by ID: {}", id);
        springDataRepository.deleteById(id.toString());
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting search result: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional
    public void deleteByQueryId(UUID queryId) {
        log.info("Deleting search results for query: {}", queryId);
        springDataRepository.deleteByQueryId(queryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByQueryId(UUID queryId) {
        return springDataRepository.countByQueryId(queryId);
    }
}
