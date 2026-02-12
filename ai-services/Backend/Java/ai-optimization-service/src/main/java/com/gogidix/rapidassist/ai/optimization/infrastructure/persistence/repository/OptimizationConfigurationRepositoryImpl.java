package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationAlgorithm;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationConfiguration;
import com.gogidix.rapidassist.ai.optimization.domain.repository.OptimizationConfigurationRepositoryPort;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.mapper.OptimizationConfigurationPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of OptimizationConfigurationRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OptimizationConfigurationRepositoryImpl implements OptimizationConfigurationRepositoryPort {

    private final SpringDataOptimizationConfigurationRepository springDataRepository;
    private final OptimizationConfigurationPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public OptimizationConfiguration save(String tenantId, OptimizationConfiguration configuration) {
        log.info("Saving optimization configuration: {} for tenant: {}", configuration.getId(), tenantId);

        var entity = persistenceMapper.toEntity(configuration);
        entity.setUpdatedAt(LocalDateTime.now());

        var savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OptimizationConfiguration> findById(String tenantId, UUID configId) {
        log.info("Finding optimization configuration by ID: {} for tenant: {}", configId, tenantId);

        return springDataRepository.findByUuidAndTenantId(configId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationConfiguration> findByAlgorithm(String tenantId, OptimizationAlgorithm algorithm) {
        log.info("Finding optimization configurations by algorithm: {} for tenant: {}", algorithm, tenantId);

        return springDataRepository.findByAlgorithmAndTenantId(algorithm, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationConfiguration> findByTenantId(String tenantId) {
        log.info("Finding all optimization configurations for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationConfiguration> findByCreatedBy(String tenantId, String createdBy) {
        log.info("Finding optimization configurations by creator: {} for tenant: {}", createdBy, tenantId);

        return springDataRepository.findByCreatedByAndTenantId(createdBy, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID configId) {
        log.info("Deleting optimization configuration: {} for tenant: {}", configId, tenantId);

        springDataRepository.deleteByUuidAndTenantId(configId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String tenantId, UUID configId) {
        return springDataRepository.existsByUuidAndTenantId(configId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }
}
