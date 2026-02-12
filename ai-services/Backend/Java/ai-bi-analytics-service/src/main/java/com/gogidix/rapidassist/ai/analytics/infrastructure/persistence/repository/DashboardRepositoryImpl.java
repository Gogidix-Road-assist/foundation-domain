package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.Dashboard;
import com.gogidix.rapidassist.ai.analytics.domain.repository.DashboardRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.DashboardEntity;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.mapper.DashboardPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of DashboardRepositoryPort using MongoDB
 */
@Repository
@RequiredArgsConstructor
public class DashboardRepositoryImpl implements DashboardRepositoryPort {

    private final SpringDataDashboardRepository springDataRepository;
    private final DashboardPersistenceMapper persistenceMapper;

    @Override
    public Dashboard save(Dashboard dashboard) {
        DashboardEntity entity = persistenceMapper.toEntity(dashboard);
        entity.setId(dashboard.getId() != null ? dashboard.getId().toString() : UUID.randomUUID().toString());
        DashboardEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Dashboard> findById(UUID id) {
        return springDataRepository.findById(id.toString())
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<Dashboard> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findById(id.toString())
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<Dashboard> findByTenantId(String tenantId) {
        List<DashboardEntity> entities = springDataRepository.findByTenantId(tenantId);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public List<Dashboard> findByTenantIdAndIsPublic(String tenantId, Boolean isPublic) {
        List<DashboardEntity> entities = springDataRepository.findByTenantIdAndIsPublic(tenantId, isPublic);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public List<Dashboard> findByTenantIdAndIsActive(String tenantId, Boolean isActive) {
        List<DashboardEntity> entities = springDataRepository.findByTenantIdAndIsActive(tenantId, isActive);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public List<Dashboard> findByTenantIdAndCategory(String tenantId, String category) {
        List<DashboardEntity> entities = springDataRepository.findByTenantIdAndCategory(tenantId, category);
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
}
