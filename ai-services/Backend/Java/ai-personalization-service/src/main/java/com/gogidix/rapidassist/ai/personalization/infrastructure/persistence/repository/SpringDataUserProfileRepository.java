package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity.UserProfileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for UserProfileEntity.
 */
@Repository
public interface SpringDataUserProfileRepository extends MongoRepository<UserProfileEntity, String> {

    Optional<UserProfileEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<UserProfileEntity> findByTenantId(String tenantId);

    List<UserProfileEntity> findByUserIdAndTenantId(String userId, String tenantId);

    List<UserProfileEntity> findBySegmentIdAndTenantId(String segmentId, String tenantId);

    List<UserProfileEntity> findByStatusAndTenantId(String status, String tenantId);

    @Query("{ 'tenantId': ?0, 'lastActivityAt': { $gte: ?1 } }")
    List<UserProfileEntity> findActiveUsers(String tenantId, LocalDateTime since);

    @Query("{ 'tenantId': ?0, 'lastActivityAt': { $lt: ?1 } }")
    List<UserProfileEntity> findInactiveUsers(String tenantId, LocalDateTime before);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    boolean existsByUserIdAndTenantId(String userId, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
