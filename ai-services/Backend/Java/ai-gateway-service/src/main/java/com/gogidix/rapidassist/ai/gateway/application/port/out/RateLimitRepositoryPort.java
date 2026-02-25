package com.gogidix.rapidassist.ai.gateway.application.port.out;

import com.gogidix.rapidassist.ai.gateway.domain.model.RateLimit;
import com.gogidix.rapidassist.ai.gateway.domain.model.RateLimitStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RateLimitRepositoryPort {
    RateLimit save(RateLimit entity);
    Optional<RateLimit> findById(UUID id);
    Optional<RateLimit> findByIdAndTenantId(UUID id, String tenantId);
    List<RateLimit> findByTenantId(String tenantId);
    List<RateLimit> findByTenantIdAndStatus(String tenantId, RateLimitStatus status);
    Optional<RateLimit> findByIdentifierAndTenantId(String identifier, String tenantId);
    Optional<RateLimit> findByLimitNameAndTenantId(String limitName, String tenantId);
    List<RateLimit> findByTenantIdAndScope(String tenantId, String scope);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    boolean existsByIdAndTenantId(UUID id, String tenantId);
}
