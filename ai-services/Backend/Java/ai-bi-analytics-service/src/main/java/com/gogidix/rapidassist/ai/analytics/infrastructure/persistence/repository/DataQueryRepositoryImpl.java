package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.DataQuery;
import com.gogidix.rapidassist.ai.analytics.domain.repository.DataQueryRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.DataQueryEntity;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.mapper.DataQueryPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of DataQueryRepositoryPort using MongoDB
 */
@Repository
@RequiredArgsConstructor
public class DataQueryRepositoryImpl implements DataQueryRepositoryPort {

    private final SpringDataDataQueryRepository springDataRepository;
    private final DataQueryPersistenceMapper persistenceMapper;

    @Override
    public DataQuery save(DataQuery query) {
        DataQueryEntity entity = persistenceMapper.toEntity(query);
        entity.setId(query.getId() != null ? query.getId().toString() : UUID.randomUUID().toString());
        DataQueryEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<DataQuery> findById(UUID id) {
        return springDataRepository.findById(id.toString())
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<DataQuery> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findById(id.toString())
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<DataQuery> findByTenantId(String tenantId) {
        List<DataQueryEntity> entities = springDataRepository.findByTenantId(tenantId);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public List<DataQuery> findByTenantIdAndIsActive(String tenantId, Boolean isActive) {
        List<DataQueryEntity> entities = springDataRepository.findByTenantIdAndIsActive(tenantId, isActive);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public List<DataQuery> findByTenantIdAndCategory(String tenantId, String category) {
        List<DataQueryEntity> entities = springDataRepository.findByTenantIdAndCategory(tenantId, category);
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
