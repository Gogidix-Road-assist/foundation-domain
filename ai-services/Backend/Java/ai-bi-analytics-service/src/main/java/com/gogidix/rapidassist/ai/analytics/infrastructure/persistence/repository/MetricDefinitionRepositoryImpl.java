package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.MetricDefinition;
import com.gogidix.rapidassist.ai.analytics.domain.repository.MetricDefinitionRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.MetricDefinitionEntity;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.mapper.MetricDefinitionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of MetricDefinitionRepositoryPort using MongoDB
 */
@Repository
@RequiredArgsConstructor
public class MetricDefinitionRepositoryImpl implements MetricDefinitionRepositoryPort {

    private final SpringDataMetricDefinitionRepository springDataRepository;
    private final MetricDefinitionPersistenceMapper persistenceMapper;

    @Override
    public MetricDefinition save(MetricDefinition metric) {
        MetricDefinitionEntity entity = persistenceMapper.toEntity(metric);
        entity.setId(metric.getId() != null ? metric.getId().toString() : UUID.randomUUID().toString());
        MetricDefinitionEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<MetricDefinition> findById(UUID id) {
        return springDataRepository.findById(id.toString())
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<MetricDefinition> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findById(id.toString())
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<MetricDefinition> findByCodeAndTenantId(String code, String tenantId) {
        return springDataRepository.findByCodeAndTenantId(code, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<MetricDefinition> findByTenantId(String tenantId) {
        List<MetricDefinitionEntity> entities = springDataRepository.findByTenantId(tenantId);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public List<MetricDefinition> findByTenantIdAndIsActive(String tenantId, Boolean isActive) {
        List<MetricDefinitionEntity> entities = springDataRepository.findByTenantIdAndIsActive(tenantId, isActive);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public List<MetricDefinition> findByTenantIdAndCategory(String tenantId, String category) {
        List<MetricDefinitionEntity> entities = springDataRepository.findByTenantIdAndCategory(tenantId, category);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    @Override
    public boolean existsById(UUID id) {
        return springDataRepository.existsById(id.toString());
    }

    @Override
    public boolean existsByCodeAndTenantId(String code, String tenantId) {
        return springDataRepository.existsByCodeAndTenantId(code, tenantId);
    }
}
