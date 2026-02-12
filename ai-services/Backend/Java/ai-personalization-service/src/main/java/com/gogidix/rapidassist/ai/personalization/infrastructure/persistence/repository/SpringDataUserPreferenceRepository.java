package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity.UserPreferenceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for UserPreferenceEntity.
 */
@Repository
public interface SpringDataUserPreferenceRepository extends MongoRepository<UserPreferenceEntity, String> {

    Optional<UserPreferenceEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<UserPreferenceEntity> findByTenantId(String tenantId);

    List<UserPreferenceEntity> findByUserIdAndTenantId(String userId, String tenantId);

    List<UserPreferenceEntity> findByProfileIdAndTenantId(UUID profileId, String tenantId);

    List<UserPreferenceEntity> findByPreferenceKeyAndTenantId(String preferenceKey, String tenantId);

    List<UserPreferenceEntity> findByCategoryAndTenantId(String category, String tenantId);

    @Query("{ 'tenantId': ?0, 'validFrom': { $lte: ?1 }, 'validUntil': { $gte: ?1 } }")
    List<UserPreferenceEntity> findValidPreferences(String tenantId, LocalDateTime dateTime);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByProfileIdAndTenantId(UUID profileId, String tenantId);
}
