package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.application.port.out.ApiKeyRepositoryPort;
import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKey;
import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKeyStatus;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.ApiKeyEntity;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper.ApiKeyPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ApiKeyRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ApiKeyRepositoryImpl implements ApiKeyRepositoryPort {

    private final SpringDataApiKeyRepository springDataRepository;
    private final ApiKeyPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public ApiKey save(ApiKey apiKey) {
        log.info("Saving API key: {} for tenant: {}", apiKey.getId(), apiKey.getTenantId());

        ApiKeyEntity entity = persistenceMapper.toEntity(apiKey);
        ApiKeyEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApiKey> findById(UUID id) {
        log.debug("Finding API key by ID: {}", id);

        return springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApiKey> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding API key by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKey> findByTenantId(String tenantId) {
        log.debug("Finding all API keys for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKey> findByTenantIdAndStatus(String tenantId, ApiKeyStatus status) {
        log.debug("Finding API keys for tenant: {} with status: {}", tenantId, status);

        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApiKey> findByApiKeyAndTenantId(String apiKey, String tenantId) {
        log.debug("Finding API key by key: {} for tenant: {}", apiKey, tenantId);

        return springDataRepository.findByApiKeyAndTenantId(apiKey, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApiKey> findByKeyNameAndTenantId(String keyName, String tenantId) {
        log.debug("Finding API key by name: {} for tenant: {}", keyName, tenantId);

        return springDataRepository.findByKeyNameAndTenantId(keyName, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKey> findByTenantIdAndStatusIn(String tenantId, List<ApiKeyStatus> statuses) {
        log.debug("Finding API keys for tenant: {} with statuses: {}", tenantId, statuses);

        return springDataRepository.findByTenantIdAndStatusIn(tenantId, statuses).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKey> findExpiredKeys(LocalDateTime currentDate) {
        log.debug("Finding expired keys as of: {}", currentDate);

        return springDataRepository.findAll().stream()
                .filter(e -> e.getExpiresAt() != null && e.getExpiresAt().isBefore(currentDate))
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting API key by ID: {}", id);

        springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .ifPresent(entity -> springDataRepository.delete(entity));
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting API key: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByApiKeyAndTenantId(String apiKey, String tenantId) {
        return springDataRepository.existsByApiKeyAndTenantId(apiKey, tenantId);
    }

    @Override
    @Transactional
    public void incrementUsageCount(UUID id, String tenantId, LocalDateTime lastUsedAt) {
        log.debug("Incrementing usage count for API key: {} in tenant: {}", id, tenantId);

        springDataRepository.findByUuidAndTenantId(id, tenantId).ifPresent(entity -> {
            entity.setUsageCount(entity.getUsageCount() + 1);
            entity.setLastUsedAt(lastUsedAt);
            springDataRepository.save(entity);
        });
    }
}
