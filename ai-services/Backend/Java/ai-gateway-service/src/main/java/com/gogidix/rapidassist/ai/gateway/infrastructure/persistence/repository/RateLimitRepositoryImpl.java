package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.application.port.out.RateLimitRepositoryPort;
import com.gogidix.rapidassist.ai.gateway.domain.model.RateLimit;
import com.gogidix.rapidassist.ai.gateway.domain.model.RateLimitStatus;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.RateLimitEntity;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper.RateLimitPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of RateLimitRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RateLimitRepositoryImpl implements RateLimitRepositoryPort {

    private final SpringDataRateLimitRepository springDataRepository;
    private final RateLimitPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public RateLimit save(RateLimit rateLimit) {
        log.info("Saving rate limit: {} for tenant: {}", rateLimit.getId(), rateLimit.getTenantId());

        RateLimitEntity entity = persistenceMapper.toEntity(rateLimit);
        RateLimitEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RateLimit> findById(UUID id) {
        log.debug("Finding rate limit by ID: {}", id);

        return springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RateLimit> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding rate limit by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RateLimit> findByTenantId(String tenantId) {
        log.debug("Finding all rate limits for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RateLimit> findByTenantIdAndStatus(String tenantId, RateLimitStatus status) {
        log.debug("Finding rate limits for tenant: {} with status: {}", tenantId, status);

        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RateLimit> findByIdentifierAndTenantId(String identifier, String tenantId) {
        log.debug("Finding rate limit by identifier: {} for tenant: {}", identifier, tenantId);

        return springDataRepository.findByIdentifierAndTenantId(identifier, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RateLimit> findByLimitNameAndTenantId(String limitName, String tenantId) {
        log.debug("Finding rate limit by name: {} for tenant: {}", limitName, tenantId);

        return springDataRepository.findByLimitNameAndTenantId(limitName, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RateLimit> findByTenantIdAndScope(String tenantId, String scope) {
        log.debug("Finding rate limits for tenant: {} with scope: {}", tenantId, scope);

        return springDataRepository.findByTenantIdAndScope(tenantId, scope).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting rate limit by ID: {}", id);

        springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .ifPresent(entity -> springDataRepository.delete(entity));
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting rate limit: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }
}
