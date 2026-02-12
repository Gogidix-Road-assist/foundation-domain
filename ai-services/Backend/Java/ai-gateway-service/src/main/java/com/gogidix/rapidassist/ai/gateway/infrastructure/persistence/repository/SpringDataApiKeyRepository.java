package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.ApiKeyEntity;
import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKeyStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ApiKeyEntity.
 */
@Repository
public interface SpringDataApiKeyRepository extends MongoRepository<ApiKeyEntity, String> {

    /**
     * Find API key by UUID and tenant.
     */
    Optional<ApiKeyEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all API keys by tenant.
     */
    List<ApiKeyEntity> findByTenantId(String tenantId);

    /**
     * Find API keys by tenant and status.
     */
    List<ApiKeyEntity> findByTenantIdAndStatus(String tenantId, ApiKeyStatus status);

    /**
     * Find API key by key and tenant.
     */
    Optional<ApiKeyEntity> findByApiKeyAndTenantId(String apiKey, String tenantId);

    /**
     * Find API key by name and tenant.
     */
    Optional<ApiKeyEntity> findByKeyNameAndTenantId(String keyName, String tenantId);

    /**
     * Find API keys by tenant and status in list.
     */
    List<ApiKeyEntity> findByTenantIdAndStatusIn(String tenantId, List<ApiKeyStatus> statuses);

    /**
     * Find expired keys.
     */
    @Query("{ 'tenantId': ?0, 'expiresAt': { $lt: ?1 } }")
    List<ApiKeyEntity> findExpiredKeys(String tenantId, LocalDateTime currentDate);

    /**
     * Check if API key exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Check if API key exists by key value.
     */
    boolean existsByApiKeyAndTenantId(String apiKey, String tenantId);

    /**
     * Delete API key by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
