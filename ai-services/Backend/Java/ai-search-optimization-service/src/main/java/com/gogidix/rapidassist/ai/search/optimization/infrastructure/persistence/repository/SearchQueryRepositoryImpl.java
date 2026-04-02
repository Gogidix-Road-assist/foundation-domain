package com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.search.optimization.application.port.out.SearchQueryRepositoryPort;
import com.gogidix.rapidassist.ai.search.optimization.domain.model.SearchQuery;
import com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.entity.SearchQueryEntity;
import com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.mapper.SearchQueryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of SearchQueryRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchQueryRepositoryImpl implements SearchQueryRepositoryPort {

    private final SpringDataSearchQueryRepository springDataRepository;
    private final SearchQueryMapper mapper;

    @Override
    @Transactional
    public SearchQuery save(SearchQuery query) {
        log.info("Saving search query: {} for tenant: {}", query.getId(), query.getTenantId());

        SearchQueryEntity entity = mapper.toEntity(query);

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());

        SearchQueryEntity savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SearchQuery> findById(UUID id) {
        log.debug("Finding search query by ID: {}", id);
        // Use findByIdAndTenantId with a default tenant or return empty
        // This method is deprecated in favor of findByIdAndTenantId
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SearchQuery> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding search query by ID: {} and tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQuery> findByTenantId(String tenantId) {
        log.debug("Finding all search queries for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQuery> findByUserId(String tenantId, String userId) {
        log.debug("Finding search queries for user: {} in tenant: {}", userId, tenantId);
        return springDataRepository.findByTenantIdAndUserId(tenantId, userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQuery> findByStatus(String tenantId, String status) {
        log.debug("Finding search queries by status: {} for tenant: {}", status, tenantId);
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting search query by ID: {}", id);
        springDataRepository.deleteById(id.toString());
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting search query: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }
}
