package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingResult;
import com.gogidix.rapidassist.ai.matching.domain.model.MatchingStatus;
import com.gogidix.rapidassist.ai.matching.domain.repository.MatchingResultRepositoryPort;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.MatchingResultEntity;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper.MatchingResultPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of MatchingResultRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MatchingResultRepositoryImpl implements MatchingResultRepositoryPort {

    private final SpringDataMatchingResultRepository springDataRepository;
    private final MatchingResultPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public MatchingResult save(String tenantId, MatchingResult matchingResult) {
        log.info("Saving matching result: {} for tenant: {}", matchingResult.getId(), tenantId);

        MatchingResultEntity entity = persistenceMapper.toEntity(matchingResult);
        MatchingResultEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MatchingResult> findById(String tenantId, UUID id) {
        log.info("Finding matching result by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MatchingResult> findByMatchId(String tenantId, String matchId) {
        log.info("Finding matching result by match ID: {} for tenant: {}", matchId, tenantId);

        return springDataRepository.findByMatchIdAndTenantId(matchId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchingResult> findByTenantId(String tenantId) {
        log.info("Finding all matching results for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchingResult> findBySourceEntity(String tenantId, String entityType, String entityId) {
        log.info("Finding matching results for source entity: {} of type: {} in tenant: {}",
                 entityId, entityType, tenantId);

        return springDataRepository.findBySourceEntityTypeAndSourceEntityIdAndTenantId(
                entityType, entityId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchingResult> findByTargetEntity(String tenantId, String entityType, String entityId) {
        log.info("Finding matching results for target entity: {} of type: {} in tenant: {}",
                 entityId, entityType, tenantId);

        return springDataRepository.findByTargetEntityTypeAndTargetEntityIdAndTenantId(
                entityType, entityId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchingResult> findByStatus(String tenantId, String status) {
        log.info("Finding matching results by status: {} for tenant: {}", status, tenantId);

        MatchingStatus matchingStatus = MatchingStatus.valueOf(status);
        return springDataRepository.findByStatusAndTenantId(matchingStatus, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting matching result: {} for tenant: {}", id, tenantId);

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
