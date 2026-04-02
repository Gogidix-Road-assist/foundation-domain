package com.gogidix.rapidassist.ai.gateway.application.service;

import com.gogidix.rapidassist.ai.gateway.application.dto.*;
import com.gogidix.rapidassist.ai.gateway.application.port.out.*;
import com.gogidix.rapidassist.ai.gateway.application.mapper.GatewayConfigMapper;
import com.gogidix.rapidassist.ai.gateway.application.mapper.RouteMapper;
import com.gogidix.rapidassist.ai.gateway.application.mapper.ApiKeyMapper;
import com.gogidix.rapidassist.ai.gateway.application.mapper.RateLimitMapper;
import com.gogidix.rapidassist.ai.gateway.application.mapper.RequestLogMapper;
import com.gogidix.rapidassist.ai.gateway.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application Service for AI Gateway operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayApplicationService {

    // Repositories
    private final GatewayConfigRepositoryPort gatewayConfigRepository;
    private final RouteRepositoryPort routeRepository;
    private final ApiKeyRepositoryPort apiKeyRepository;
    private final RateLimitRepositoryPort rateLimitRepository;
    private final RequestLogRepositoryPort requestLogRepository;

    // Mappers
    private final GatewayConfigMapper gatewayConfigMapper;
    private final RouteMapper routeMapper;
    private final ApiKeyMapper apiKeyMapper;
    private final RateLimitMapper rateLimitMapper;
    private final RequestLogMapper requestLogMapper;

    // ============================================================
    // Gateway Config Operations
    // ============================================================

    @Transactional
    public GatewayConfigDto createGatewayConfig(GatewayConfigDto dto) {
        log.info("Creating gateway config: {} for tenant: {}", dto.getConfigName(), dto.getTenantId());

        GatewayConfig config = gatewayConfigMapper.toDomain(dto);
        config.setId(UUID.randomUUID());
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());

        GatewayConfig saved = gatewayConfigRepository.save(config);
        return gatewayConfigMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public GatewayConfigDto getGatewayConfig(UUID id, String tenantId) {
        log.info("Getting gateway config: {} for tenant: {}", id, tenantId);

        GatewayConfig config = gatewayConfigRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Gateway config not found"));

        return gatewayConfigMapper.toDto(config);
    }

    @Transactional(readOnly = true)
    public List<GatewayConfigDto> getAllGatewayConfigs(String tenantId) {
        log.info("Getting all gateway configs for tenant: {}", tenantId);

        return gatewayConfigRepository.findByTenantId(tenantId).stream()
                .map(gatewayConfigMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public GatewayConfigDto updateGatewayConfig(UUID id, String tenantId, GatewayConfigDto dto) {
        log.info("Updating gateway config: {} for tenant: {}", id, tenantId);

        GatewayConfig existing = gatewayConfigRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Gateway config not found"));

        // Update fields
        existing.setConfigName(dto.getConfigName());
        existing.setDescription(dto.getDescription());
        existing.setRoutingRules(dto.getRoutingRules());
        existing.setLoadBalancingConfig(dto.getLoadBalancingConfig());
        existing.setRateLimitConfig(dto.getRateLimitConfig());
        existing.setAuthConfig(dto.getAuthConfig());
        existing.setMonitoringConfig(dto.getMonitoringConfig());
        existing.setPriority(dto.getPriority());
        existing.setMetadata(dto.getMetadata());
        existing.setUpdatedAt(LocalDateTime.now());

        GatewayConfig saved = gatewayConfigRepository.save(existing);
        return gatewayConfigMapper.toDto(saved);
    }

    @Transactional
    public void deleteGatewayConfig(UUID id, String tenantId) {
        log.info("Deleting gateway config: {} for tenant: {}", id, tenantId);

        if (!gatewayConfigRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new IllegalArgumentException("Gateway config not found");
        }

        gatewayConfigRepository.deleteByIdAndTenantId(id, tenantId);
    }

    // ============================================================
    // Route Operations
    // ============================================================

    @Transactional
    public RouteDto createRoute(RouteDto dto) {
        log.info("Creating route: {} for tenant: {}", dto.getRouteName(), dto.getTenantId());

        Route route = routeMapper.toDomain(dto);
        route.setId(UUID.randomUUID());
        route.setCreatedAt(LocalDateTime.now());
        route.setUpdatedAt(LocalDateTime.now());

        Route saved = routeRepository.save(route);
        return routeMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public RouteDto getRoute(UUID id, String tenantId) {
        log.info("Getting route: {} for tenant: {}", id, tenantId);

        Route route = routeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));

        return routeMapper.toDto(route);
    }

    @Transactional(readOnly = true)
    public List<RouteDto> getAllRoutes(String tenantId) {
        log.info("Getting all routes for tenant: {}", tenantId);

        return routeRepository.findByTenantId(tenantId).stream()
                .map(routeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RouteDto> getActiveRoutes(String tenantId) {
        log.info("Getting active routes for tenant: {}", tenantId);

        return routeRepository.findByTenantIdAndStatus(tenantId, RouteStatus.ACTIVE).stream()
                .map(routeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RouteDto updateRoute(UUID id, String tenantId, RouteDto dto) {
        log.info("Updating route: {} for tenant: {}", id, tenantId);

        Route existing = routeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));

        // Update fields
        existing.setRouteName(dto.getRouteName());
        existing.setPath(dto.getPath());
        existing.setServiceId(dto.getServiceId());
        existing.setServiceUrl(dto.getServiceUrl());
        existing.setHttpMethod(dto.getHttpMethod());
        existing.setPriority(dto.getPriority());
        existing.setIsAuthenticated(dto.getIsAuthenticated());
        existing.setRateLimited(dto.getRateLimited());
        existing.setRateLimit(dto.getRateLimit());
        existing.setRateLimitWindowSeconds(dto.getRateLimitWindowSeconds());
        existing.setCircuitBreakerPolicy(dto.getCircuitBreakerPolicy());
        existing.setTimeoutMs(dto.getTimeoutMs());
        existing.setRetryCount(dto.getRetryCount());
        existing.setHeaders(dto.getHeaders());
        existing.setQueryParams(dto.getQueryParams());
        existing.setDescription(dto.getDescription());
        existing.setUpdatedAt(LocalDateTime.now());

        Route saved = routeRepository.save(existing);
        return routeMapper.toDto(saved);
    }

    @Transactional
    public void deleteRoute(UUID id, String tenantId) {
        log.info("Deleting route: {} for tenant: {}", id, tenantId);

        if (!routeRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new IllegalArgumentException("Route not found");
        }

        routeRepository.deleteByIdAndTenantId(id, tenantId);
    }

    // ============================================================
    // API Key Operations
    // ============================================================

    @Transactional
    public ApiKeyDto createApiKey(ApiKeyDto dto) {
        log.info("Creating API key: {} for tenant: {}", dto.getKeyName(), dto.getTenantId());

        ApiKey apiKey = apiKeyMapper.toDomain(dto);
        apiKey.setId(UUID.randomUUID());
        apiKey.setCreatedAt(LocalDateTime.now());
        apiKey.setUpdatedAt(LocalDateTime.now());
        apiKey.setUsageCount(0L);

        // Generate API key if not provided
        if (apiKey.getApiKey() == null || apiKey.getApiKey().isEmpty()) {
            apiKey.setApiKey(UUID.randomUUID().toString().replace("-", ""));
        }

        ApiKey saved = apiKeyRepository.save(apiKey);
        return apiKeyMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public ApiKeyDto getApiKey(UUID id, String tenantId) {
        log.info("Getting API key: {} for tenant: {}", id, tenantId);

        ApiKey apiKey = apiKeyRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("API key not found"));

        return apiKeyMapper.toDto(apiKey);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyDto> getAllApiKeys(String tenantId) {
        log.info("Getting all API keys for tenant: {}", tenantId);

        return apiKeyRepository.findByTenantId(tenantId).stream()
                .map(apiKeyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApiKeyDto updateApiKey(UUID id, String tenantId, ApiKeyDto dto) {
        log.info("Updating API key: {} for tenant: {}", id, tenantId);

        ApiKey existing = apiKeyRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("API key not found"));

        // Update fields
        existing.setKeyName(dto.getKeyName());
        existing.setAllowedServices(dto.getAllowedServices());
        existing.setAllowedPaths(dto.getAllowedPaths());
        existing.setRateLimit(dto.getRateLimit());
        existing.setRateLimitWindowSeconds(dto.getRateLimitWindowSeconds());
        existing.setExpiresAt(dto.getExpiresAt());
        existing.setDescription(dto.getDescription());
        existing.setMetadata(dto.getMetadata());
        existing.setUpdatedAt(LocalDateTime.now());

        ApiKey saved = apiKeyRepository.save(existing);
        return apiKeyMapper.toDto(saved);
    }

    @Transactional
    public void deleteApiKey(UUID id, String tenantId) {
        log.info("Deleting API key: {} for tenant: {}", id, tenantId);

        if (!apiKeyRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new IllegalArgumentException("API key not found");
        }

        apiKeyRepository.deleteByIdAndTenantId(id, tenantId);
    }

    @Transactional
    public void revokeApiKey(UUID id, String tenantId) {
        log.info("Revoking API key: {} for tenant: {}", id, tenantId);

        ApiKey existing = apiKeyRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("API key not found"));

        existing.setStatus(ApiKeyStatus.REVOKED);
        existing.setUpdatedAt(LocalDateTime.now());

        apiKeyRepository.save(existing);
    }

    // ============================================================
    // Rate Limit Operations
    // ============================================================

    @Transactional
    public RateLimitDto createRateLimit(RateLimitDto dto) {
        log.info("Creating rate limit: {} for tenant: {}", dto.getLimitName(), dto.getTenantId());

        RateLimit rateLimit = rateLimitMapper.toDomain(dto);
        rateLimit.setId(UUID.randomUUID());
        rateLimit.setCreatedAt(LocalDateTime.now());
        rateLimit.setUpdatedAt(LocalDateTime.now());

        RateLimit saved = rateLimitRepository.save(rateLimit);
        return rateLimitMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public RateLimitDto getRateLimit(UUID id, String tenantId) {
        log.info("Getting rate limit: {} for tenant: {}", id, tenantId);

        RateLimit rateLimit = rateLimitRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Rate limit not found"));

        return rateLimitMapper.toDto(rateLimit);
    }

    @Transactional(readOnly = true)
    public List<RateLimitDto> getAllRateLimits(String tenantId) {
        log.info("Getting all rate limits for tenant: {}", tenantId);

        return rateLimitRepository.findByTenantId(tenantId).stream()
                .map(rateLimitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RateLimitDto updateRateLimit(UUID id, String tenantId, RateLimitDto dto) {
        log.info("Updating rate limit: {} for tenant: {}", id, tenantId);

        RateLimit existing = rateLimitRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Rate limit not found"));

        // Update fields
        existing.setLimitName(dto.getLimitName());
        existing.setIdentifier(dto.getIdentifier());
        existing.setRequestsPerWindow(dto.getRequestsPerWindow());
        existing.setWindowSizeSeconds(dto.getWindowSizeSeconds());
        existing.setAlgorithm(dto.getAlgorithm());
        existing.setScope(dto.getScope());
        existing.setMetadata(dto.getMetadata());
        existing.setUpdatedAt(LocalDateTime.now());

        RateLimit saved = rateLimitRepository.save(existing);
        return rateLimitMapper.toDto(saved);
    }

    @Transactional
    public void deleteRateLimit(UUID id, String tenantId) {
        log.info("Deleting rate limit: {} for tenant: {}", id, tenantId);

        if (!rateLimitRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new IllegalArgumentException("Rate limit not found");
        }

        rateLimitRepository.deleteByIdAndTenantId(id, tenantId);
    }

    // ============================================================
    // Request Log Operations
    // ============================================================

    @Transactional
    public RequestLogDto logRequest(RequestLogDto dto) {
        log.debug("Logging request: {} for tenant: {}", dto.getRequestId(), dto.getTenantId());

        RequestLog requestLog = requestLogMapper.toDomain(dto);
        requestLog.setId(UUID.randomUUID());
        requestLog.setCreatedAt(LocalDateTime.now());

        RequestLog saved = requestLogRepository.save(requestLog);
        return requestLogMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<RequestLogDto> getRequestLogs(String tenantId, int limit) {
        log.info("Getting request logs for tenant: {} limit: {}", tenantId, limit);

        return requestLogRepository.findByTenantId(tenantId).stream()
                .limit(limit)
                .map(requestLogMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RequestLogDto> getRequestLogsByService(String tenantId, String serviceId, int limit) {
        log.info("Getting request logs for tenant: {} service: {} limit: {}", tenantId, serviceId, limit);

        return requestLogRepository.findByTenantIdAndServiceId(tenantId, serviceId).stream()
                .limit(limit)
                .map(requestLogMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cleanupOldLogs(String tenantId, int daysToKeep) {
        log.info("Cleaning up logs older than {} days for tenant: {}", daysToKeep, tenantId);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        requestLogRepository.deleteByTenantIdAndCreatedAtBefore(tenantId, cutoffDate);
    }
}
