package com.gogidix.rapidassist.integration.adapters.service.application.service;

import com.gogidix.rapidassist.integration.adapters.service.application.dto.CreateIntegrationConfigRequest;
import com.gogidix.rapidassist.integration.adapters.service.application.dto.IntegrationConfigResponse;
import com.gogidix.rapidassist.integration.adapters.service.application.dto.UpdateIntegrationConfigRequest;
import com.gogidix.rapidassist.integration.adapters.service.domain.model.IntegrationConfig;
import com.gogidix.rapidassist.integration.adapters.service.domain.port.out.IntegrationConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IntegrationConfigService {

    private final IntegrationConfigRepository repository;

    public IntegrationConfigService(IntegrationConfigRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<IntegrationConfigResponse> findByTenant(String tenantId) {
        return repository.findByTenantId(tenantId).stream()
                .map(IntegrationConfigResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IntegrationConfigResponse findByTenantAndId(String tenantId, String id) {
        return repository.findByTenantIdAndId(tenantId, id)
                .map(IntegrationConfigResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Integration config not found"));
    }

    @Transactional(readOnly = true)
    public IntegrationConfigResponse findByTenantAndProvider(String tenantId, String provider) {
        return repository.findByTenantIdAndProvider(tenantId, provider)
                .map(IntegrationConfigResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Integration config not found"));
    }

    @Transactional(readOnly = true)
    public List<IntegrationConfigResponse> findEnabledByTenant(String tenantId) {
        return repository.findByTenantIdAndEnabled(tenantId, true).stream()
                .map(IntegrationConfigResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IntegrationConfigResponse> searchByTenant(String tenantId, String searchTerm) {
        return repository.searchByTenantId(tenantId, searchTerm).stream()
                .map(IntegrationConfigResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IntegrationConfigResponse> findEnabledByTenantOrderByLastSync(String tenantId) {
        return repository.findEnabledByTenantIdOrderByLastSync(tenantId).stream()
                .map(IntegrationConfigResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countByTenant(String tenantId) {
        return repository.countByTenantId(tenantId);
    }

    public IntegrationConfigResponse create(String tenantId, CreateIntegrationConfigRequest request) {
        repository.findByTenantIdAndProvider(tenantId, request.getProvider())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Integration config with provider '" + request.getProvider() + "' already exists for tenant");
                });

        IntegrationConfig config = new IntegrationConfig();
        config.setTenantId(tenantId);
        config.setProvider(request.getProvider());
        config.setProviderName(request.getProviderName());
        config.setApiEndpoint(request.getApiEndpoint());
        config.setApiKey(request.getApiKey());
        config.setApiSecret(request.getApiSecret());
        config.setAuthToken(request.getAuthToken());
        config.setWebhookUrl(request.getWebhookUrl());
        config.setTimeoutMs(request.getTimeoutMs());
        config.setRetryAttempts(request.getRetryAttempts());
        config.setEnabled(request.getEnabled());
        config.setConfiguration(request.getConfiguration());
        config.setMetadata(request.getMetadata());
        config.setSyncStatus("PENDING");

        IntegrationConfig saved = repository.save(config);
        return IntegrationConfigResponse.from(saved);
    }

    public IntegrationConfigResponse update(String tenantId, String id, UpdateIntegrationConfigRequest request) {
        IntegrationConfig config = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Integration config not found"));

        repository.findByTenantIdAndProviderExcludingId(tenantId, request.getProvider(), id)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Integration config with provider '" + request.getProvider() + "' already exists");
                });

        config.setProvider(request.getProvider());
        config.setProviderName(request.getProviderName());
        config.setApiEndpoint(request.getApiEndpoint());
        config.setApiKey(request.getApiKey());
        config.setApiSecret(request.getApiSecret());
        config.setAuthToken(request.getAuthToken());
        config.setWebhookUrl(request.getWebhookUrl());
        config.setTimeoutMs(request.getTimeoutMs());
        config.setRetryAttempts(request.getRetryAttempts());
        config.setEnabled(request.getEnabled());
        config.setConfiguration(request.getConfiguration());
        config.setMetadata(request.getMetadata());

        IntegrationConfig updated = repository.save(config);
        return IntegrationConfigResponse.from(updated);
    }

    public void delete(String tenantId, String id) {
        IntegrationConfig config = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Integration config not found"));

        repository.delete(config);
    }

    public IntegrationConfigResponse enable(String tenantId, String id) {
        IntegrationConfig config = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Integration config not found"));

        config.setEnabled(true);
        IntegrationConfig updated = repository.save(config);
        return IntegrationConfigResponse.from(updated);
    }

    public IntegrationConfigResponse disable(String tenantId, String id) {
        IntegrationConfig config = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Integration config not found"));

        config.setEnabled(false);
        IntegrationConfig updated = repository.save(config);
        return IntegrationConfigResponse.from(updated);
    }

    public IntegrationConfigResponse updateSyncStatus(String tenantId, String id, String status) {
        IntegrationConfig config = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Integration config not found"));

        config.setSyncStatus(status);
        config.setLastSyncAt(LocalDateTime.now());
        IntegrationConfig updated = repository.save(config);
        return IntegrationConfigResponse.from(updated);
    }

    public IntegrationConfigResponse testConnection(String tenantId, String id) {
        IntegrationConfig config = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Integration config not found"));

        // TODO: Implement actual connection test logic here
        config.setSyncStatus("CONNECTED");
        config.setLastSyncAt(LocalDateTime.now());
        IntegrationConfig updated = repository.save(config);
        return IntegrationConfigResponse.from(updated);
    }
}
