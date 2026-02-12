package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity.UserPreferenceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for UserPreferenceEntity.
 */
@Repository
public interface SpringDataUserPreferenceRepository extends MongoRepository<UserPreferenceEntity, UUID> {

    List<UserPreferenceEntity> findByTenantIdAndUserId(String tenantId, String userId);

    List<UserPreferenceEntity> findByTenantIdAndUserIdAndItemType(String tenantId, String userId, String itemType);

    UserPreferenceEntity findByTenantIdAndUserIdAndItemTypeAndItemId(String tenantId, String userId, String itemType, String itemId);

    List<UserPreferenceEntity> findByTenantIdAndItemType(String tenantId, String itemType);

    List<UserPreferenceEntity> findByTenantIdAndLastInteractionAtBefore(String tenantId, LocalDateTime threshold);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndUserId(String tenantId, String userId);
}
