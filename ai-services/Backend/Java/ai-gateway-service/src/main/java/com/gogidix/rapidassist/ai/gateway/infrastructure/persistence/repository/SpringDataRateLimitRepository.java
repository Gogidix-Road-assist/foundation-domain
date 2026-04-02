package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.RateLimitEntity;
import com.gogidix.rapidassist.ai.gateway.domain.model.RateLimitStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for RateLimitEntity.
 */
@Repository
public interface SpringDataRateLimitRepository extends MongoRepository<RateLimitEntity, String> {

    /**
     * Find rate limit by UUID and tenant.
     */
    Optional<RateLimitEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all rate limits by tenant.
     */
    List<RateLimitEntity> findByTenantId(String tenantId);

    /**
     * Find rate limits by tenant and status.
     */
    List<RateLimitEntity> findByTenantIdAndStatus(String tenantId, RateLimitStatus status);

    /**
     * Find rate limit by identifier and tenant.
     */
    Optional<RateLimitEntity> findByIdentifierAndTenantId(String identifier, String tenantId);

    /**
     * Find rate limit by name and tenant.
     */
    Optional<RateLimitEntity> findByLimitNameAndTenantId(String limitName, String tenantId);

    /**
     * Find rate limits by tenant and scope.
     */
    List<RateLimitEntity> findByTenantIdAndScope(String tenantId, String scope);

    /**
     * Check if rate limit exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete rate limit by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find active rate limits for a scope.
     */
    List<RateLimitEntity> findByTenantIdAndScopeAndStatus(
            String tenantId, String scope, RateLimitStatus status);
}
