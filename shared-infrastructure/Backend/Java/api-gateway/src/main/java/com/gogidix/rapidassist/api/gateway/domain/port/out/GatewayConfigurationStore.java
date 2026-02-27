package com.gogidix.rapidassist.api.gateway.domain.port.out;

import com.gogidix.rapidassist.api.gateway.domain.model.GatewayConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface GatewayConfigurationStore {

    CompletableFuture<GatewayConfiguration> save(GatewayConfiguration configuration);

    CompletableFuture<Optional<GatewayConfiguration>> findById(String id);

    CompletableFuture<Optional<GatewayConfiguration>> findByKeyAndEnvironment(
        String tenantId, String configKey, String environment);

    CompletableFuture<List<GatewayConfiguration>> findByTenant(String tenantId);

    CompletableFuture<List<GatewayConfiguration>> findByEnvironment(
        String tenantId, String environment);

    CompletableFuture<List<GatewayConfiguration>> findByType(
        String tenantId, GatewayConfiguration.ConfigurationType type);

    CompletableFuture<Boolean> deleteById(String id);

    CompletableFuture<Boolean> deleteByKeyAndEnvironment(
        String tenantId, String configKey, String environment);
}
