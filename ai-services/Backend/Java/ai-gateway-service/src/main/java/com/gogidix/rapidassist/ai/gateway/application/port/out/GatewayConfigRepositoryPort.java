package com.gogidix.rapidassist.ai.gateway.application.port.out;

import com.gogidix.rapidassist.ai.gateway.domain.model.GatewayConfig;
import com.gogidix.rapidassist.ai.gateway.domain.model.GatewayStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GatewayConfigRepositoryPort {
    GatewayConfig save(GatewayConfig entity);
    Optional<GatewayConfig> findById(UUID id);
    Optional<GatewayConfig> findByIdAndTenantId(UUID id, String tenantId);
    List<GatewayConfig> findByTenantId(String tenantId);
    List<GatewayConfig> findByTenantIdAndStatus(String tenantId, GatewayStatus status);
    Optional<GatewayConfig> findByTenantIdAndConfigName(String tenantId, String configName);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    boolean existsByIdAndTenantId(UUID id, String tenantId);
}
