package com.gogidix.rapidassist.ai.gateway.application.port.out;

import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKey;
import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKeyStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepositoryPort {
    ApiKey save(ApiKey entity);
    Optional<ApiKey> findById(UUID id);
    Optional<ApiKey> findByIdAndTenantId(UUID id, String tenantId);
    List<ApiKey> findByTenantId(String tenantId);
    List<ApiKey> findByTenantIdAndStatus(String tenantId, ApiKeyStatus status);
    Optional<ApiKey> findByApiKeyAndTenantId(String apiKey, String tenantId);
    Optional<ApiKey> findByKeyNameAndTenantId(String keyName, String tenantId);
    List<ApiKey> findByTenantIdAndStatusIn(String tenantId, List<ApiKeyStatus> statuses);
    List<ApiKey> findExpiredKeys(LocalDateTime currentDate);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    boolean existsByIdAndTenantId(UUID id, String tenantId);
    boolean existsByApiKeyAndTenantId(String apiKey, String tenantId);
    void incrementUsageCount(UUID id, String tenantId, LocalDateTime lastUsedAt);
}
