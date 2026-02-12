package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity.ABTestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ABTestEntity.
 */
@Repository
public interface SpringDataABTestRepository extends MongoRepository<ABTestEntity, String> {

    Optional<ABTestEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<ABTestEntity> findByTestCodeAndTenantId(String testCode, String tenantId);

    List<ABTestEntity> findByTenantId(String tenantId);

    List<ABTestEntity> findByTestTypeAndTenantId(String testType, String tenantId);

    List<ABTestEntity> findByStatusAndTenantId(String status, String tenantId);

    List<ABTestEntity> findByCategoryAndTenantId(String category, String tenantId);

    List<ABTestEntity> findByStatusInAndTenantId(List<String> statuses, String tenantId);

    @Query("{ 'tenantId': ?0, 'startDate': { $lte: ?1 }, 'endDate': { $gte: ?1 } }")
    List<ABTestEntity> findRunningTests(String tenantId, LocalDateTime dateTime);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    boolean existsByTestCodeAndTenantId(String testCode, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
