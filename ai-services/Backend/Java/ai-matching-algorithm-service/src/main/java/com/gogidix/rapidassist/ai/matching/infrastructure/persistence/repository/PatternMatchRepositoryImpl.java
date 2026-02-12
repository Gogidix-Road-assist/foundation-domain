package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.PatternMatch;
import com.gogidix.rapidassist.ai.matching.domain.repository.PatternMatchRepositoryPort;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.PatternMatchEntity;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper.PatternMatchPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of PatternMatchRepositoryPort.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PatternMatchRepositoryImpl implements PatternMatchRepositoryPort {

    private final SpringDataPatternMatchRepository springDataRepository;
    private final PatternMatchPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public PatternMatch save(String tenantId, PatternMatch patternMatch) {
        log.info("Saving pattern match: {} for tenant: {}", patternMatch.getId(), tenantId);

        PatternMatchEntity entity = persistenceMapper.toEntity(patternMatch);
        PatternMatchEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatternMatch> findById(String tenantId, UUID id) {
        log.info("Finding pattern match by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatternMatch> findByTenantId(String tenantId) {
        log.info("Finding all pattern matches for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatternMatch> findByPatternId(String tenantId, String patternId) {
        log.info("Finding pattern match by pattern ID: {} for tenant: {}", patternId, tenantId);

        return springDataRepository.findByPatternIdAndTenantId(patternId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatternMatch> findByEntityType(String tenantId, String entityType) {
        log.info("Finding pattern matches for entity type: {} in tenant: {}", entityType, tenantId);

        return springDataRepository.findByEntityTypeAndTenantId(entityType, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatternMatch> findByEntityId(String tenantId, String entityType, String entityId) {
        log.info("Finding pattern matches for entity type: {} and entity ID: {} in tenant: {}",
                 entityType, entityId, tenantId);

        return springDataRepository.findByEntityTypeAndEntityIdAndTenantId(entityType, entityId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting pattern match: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }
}
