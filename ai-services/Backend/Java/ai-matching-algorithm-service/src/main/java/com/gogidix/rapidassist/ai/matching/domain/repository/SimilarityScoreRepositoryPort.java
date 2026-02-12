package com.gogidix.rapidassist.ai.matching.domain.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.SimilarityScore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for SimilarityScore operations.
 * Defines the contract for similarity score persistence.
 */
public interface SimilarityScoreRepositoryPort {

    SimilarityScore save(String tenantId, SimilarityScore similarityScore);

    Optional<SimilarityScore> findById(String tenantId, UUID id);

    Optional<SimilarityScore> findByScoreId(String tenantId, String scoreId);

    List<SimilarityScore> findByTenantId(String tenantId);

    List<SimilarityScore> findByEntityPair(String tenantId, String sourceEntityType, String sourceEntityId,
                                           String targetEntityType, String targetEntityId);

    void delete(String tenantId, UUID id);

    boolean exists(String tenantId, UUID id);

    long countByTenantId(String tenantId);
}
