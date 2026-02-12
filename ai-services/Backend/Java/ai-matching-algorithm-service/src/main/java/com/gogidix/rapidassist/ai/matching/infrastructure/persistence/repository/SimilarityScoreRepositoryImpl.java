package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.SimilarityScore;
import com.gogidix.rapidassist.ai.matching.domain.repository.SimilarityScoreRepositoryPort;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.SimilarityScoreEntity;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper.SimilarityScorePersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of SimilarityScoreRepositoryPort.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SimilarityScoreRepositoryImpl implements SimilarityScoreRepositoryPort {

    private final SpringDataSimilarityScoreRepository springDataRepository;
    private final SimilarityScorePersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public SimilarityScore save(String tenantId, SimilarityScore similarityScore) {
        log.info("Saving similarity score: {} for tenant: {}", similarityScore.getId(), tenantId);

        SimilarityScoreEntity entity = persistenceMapper.toEntity(similarityScore);
        SimilarityScoreEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SimilarityScore> findById(String tenantId, UUID id) {
        log.info("Finding similarity score by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SimilarityScore> findByScoreId(String tenantId, String scoreId) {
        log.info("Finding similarity score by score ID: {} for tenant: {}", scoreId, tenantId);

        return springDataRepository.findByScoreIdAndTenantId(scoreId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimilarityScore> findByTenantId(String tenantId) {
        log.info("Finding all similarity scores for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimilarityScore> findByEntityPair(String tenantId, String sourceEntityType, String sourceEntityId,
                                                   String targetEntityType, String targetEntityId) {
        log.info("Finding similarity scores for entity pair: {} ({}) - {} ({}) in tenant: {}",
                 sourceEntityType, sourceEntityId, targetEntityType, targetEntityId, tenantId);

        return springDataRepository.findBySourceEntityTypeAndSourceEntityIdAndTargetEntityTypeAndTargetEntityIdAndTenantId(
                sourceEntityType, sourceEntityId, targetEntityType, targetEntityId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting similarity score: {} for tenant: {}", id, tenantId);

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
