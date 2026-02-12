package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity.UserSegmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for UserSegmentEntity.
 */
@Repository
public interface SpringDataUserSegmentRepository extends MongoRepository<UserSegmentEntity, String> {

    Optional<UserSegmentEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<UserSegmentEntity> findBySegmentCodeAndTenantId(String segmentCode, String tenantId);

    List<UserSegmentEntity> findByTenantId(String tenantId);

    List<UserSegmentEntity> findBySegmentTypeAndTenantId(String segmentType, String tenantId);

    List<UserSegmentEntity> findByStatusAndTenantId(String status, String tenantId);

    List<UserSegmentEntity> findByCategoryAndTenantId(String category, String tenantId);

    @Query("{ 'tenantId': ?0, 'includedUserIds': { $in: ?1 } }")
    List<UserSegmentEntity> findByUserIdInSegments(String tenantId, String userId);

    @Query("{ 'tenantId': ?0, 'autoUpdate': true }")
    List<UserSegmentEntity> findAutoUpdateSegments(String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    boolean existsBySegmentCodeAndTenantId(String segmentCode, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
