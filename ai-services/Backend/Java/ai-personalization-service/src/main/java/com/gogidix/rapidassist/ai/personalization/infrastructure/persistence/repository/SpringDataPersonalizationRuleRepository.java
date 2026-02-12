package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity.PersonalizationRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for PersonalizationRuleEntity.
 */
@Repository
public interface SpringDataPersonalizationRuleRepository extends MongoRepository<PersonalizationRuleEntity, String> {

    Optional<PersonalizationRuleEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<PersonalizationRuleEntity> findByRuleCodeAndTenantId(String ruleCode, String tenantId);

    List<PersonalizationRuleEntity> findByTenantId(String tenantId);

    List<PersonalizationRuleEntity> findByRuleTypeAndTenantId(String ruleType, String tenantId);

    List<PersonalizationRuleEntity> findByStatusAndTenantId(String status, String tenantId);

    List<PersonalizationRuleEntity> findByCategoryAndTenantId(String category, String tenantId);

    @Query("{ 'tenantId': ?0, 'status': 'ACTIVE', 'validFrom': { $lte: ?1 }, '$or': [ { 'validUntil': null }, { 'validUntil': { $gte: ?1 } } ] }")
    List<PersonalizationRuleEntity> findActiveRules(String tenantId, LocalDateTime dateTime);

    @Query("{ 'tenantId': ?0, 'priority': { $gte: ?1 } }")
    List<PersonalizationRuleEntity> findByPriorityGreaterThanEqual(String tenantId, Integer priority);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    boolean existsByRuleCodeAndTenantId(String ruleCode, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
