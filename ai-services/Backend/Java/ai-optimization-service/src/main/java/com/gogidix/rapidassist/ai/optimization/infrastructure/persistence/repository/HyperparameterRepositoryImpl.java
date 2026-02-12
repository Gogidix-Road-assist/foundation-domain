package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.Hyperparameter;
import com.gogidix.rapidassist.ai.optimization.domain.repository.HyperparameterRepositoryPort;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.mapper.HyperparameterPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of HyperparameterRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class HyperparameterRepositoryImpl implements HyperparameterRepositoryPort {

    private final SpringDataHyperparameterRepository springDataRepository;
    private final HyperparameterPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public Hyperparameter save(String tenantId, Hyperparameter hyperparameter) {
        log.info("Saving hyperparameter: {} for tenant: {}", hyperparameter.getId(), tenantId);

        var entity = persistenceMapper.toEntity(hyperparameter);
        entity.setUpdatedAt(LocalDateTime.now());

        var savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Hyperparameter> findById(String tenantId, UUID hyperparameterId) {
        log.info("Finding hyperparameter by ID: {} for tenant: {}", hyperparameterId, tenantId);

        return springDataRepository.findByUuidAndTenantId(hyperparameterId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Hyperparameter> findByName(String tenantId, String name) {
        log.info("Finding hyperparameter by name: {} for tenant: {}", name, tenantId);

        return springDataRepository.findByNameAndTenantId(name, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hyperparameter> findByTenantId(String tenantId) {
        log.info("Finding all hyperparameters for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hyperparameter> findByCategory(String tenantId, String category) {
        log.info("Finding hyperparameters by category: {} for tenant: {}", category, tenantId);

        return springDataRepository.findByCategoryAndTenantId(category, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hyperparameter> findByType(String tenantId, String type) {
        log.info("Finding hyperparameters by type: {} for tenant: {}", type, tenantId);

        return springDataRepository.findByTypeAndTenantId(type, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hyperparameter> findByNameContaining(String tenantId, String namePattern) {
        log.info("Finding hyperparameters by name pattern: {} for tenant: {}", namePattern, tenantId);

        return springDataRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, namePattern).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID hyperparameterId) {
        log.info("Deleting hyperparameter: {} for tenant: {}", hyperparameterId, tenantId);

        springDataRepository.deleteByUuidAndTenantId(hyperparameterId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String tenantId, String name) {
        return springDataRepository.existsByNameAndTenantId(name, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }
}
