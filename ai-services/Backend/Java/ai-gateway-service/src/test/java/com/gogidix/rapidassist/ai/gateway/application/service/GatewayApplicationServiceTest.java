package com.gogidix.rapidassist.ai.gateway.application.service;

import com.gogidix.rapidassist.ai.gateway.application.dto.*;
import com.gogidix.rapidassist.ai.gateway.application.mapper.*;
import com.gogidix.rapidassist.ai.gateway.application.port.out.*;
import com.gogidix.rapidassist.ai.gateway.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GatewayApplicationService.
 * Tests use cases, business logic, and service layer operations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Gateway Application Service Tests")
class GatewayApplicationServiceTest {

    @Mock
    private GatewayConfigRepositoryPort gatewayConfigRepository;

    @Mock
    private RouteRepositoryPort routeRepository;

    @Mock
    private ApiKeyRepositoryPort apiKeyRepository;

    @Mock
    private RateLimitRepositoryPort rateLimitRepository;

    @Mock
    private RequestLogRepositoryPort requestLogRepository;

    @Mock
    private GatewayConfigMapper gatewayConfigMapper;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private ApiKeyMapper apiKeyMapper;

    @Mock
    private RateLimitMapper rateLimitMapper;

    @Mock
    private RequestLogMapper requestLogMapper;

    @InjectMocks
    private GatewayApplicationService applicationService;

    private GatewayConfigDto gatewayConfigDto;
    private GatewayConfig gatewayConfig;
    private RouteDto routeDto;
    private Route route;
    private ApiKeyDto apiKeyDto;
    private ApiKey apiKey;
    private RateLimitDto rateLimitDto;
    private RateLimit rateLimit;
    private RequestLogDto requestLogDto;
    private RequestLog requestLog;

    private final String tenantId = "tenant-123";
    private final UUID testId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Initialize Gateway Config test data
        gatewayConfigDto = new GatewayConfigDto();
        gatewayConfigDto.setId(testId);
        gatewayConfigDto.setTenantId(tenantId);
        gatewayConfigDto.setConfigName("Test Config");
        gatewayConfigDto.setDescription("Test gateway config");

        gatewayConfig = new GatewayConfig();
        gatewayConfig.setId(testId);
        gatewayConfig.setTenantId(tenantId);
        gatewayConfig.setConfigName("Test Config");

        // Initialize Route test data
        routeDto = new RouteDto();
        routeDto.setId(testId);
        routeDto.setTenantId(tenantId);
        routeDto.setRouteName("Test Route");
        routeDto.setPath("/api/test");
        routeDto.setServiceUrl("http://localhost:8080");
        routeDto.setHttpMethod("GET");
        routeDto.setStatus(RouteStatus.ACTIVE.name());

        route = new Route();
        route.setId(testId);
        route.setTenantId(tenantId);
        route.setRouteName("Test Route");
        route.setPath("/api/test");
        route.setServiceUrl("http://localhost:8080");
        route.setHttpMethod("GET");
        route.setStatus(RouteStatus.ACTIVE);

        // Initialize API Key test data
        apiKeyDto = new ApiKeyDto();
        apiKeyDto.setId(testId);
        apiKeyDto.setTenantId(tenantId);
        apiKeyDto.setKeyName("Test API Key");
        apiKeyDto.setStatus(ApiKeyStatus.ACTIVE.name());

        apiKey = new ApiKey();
        apiKey.setId(testId);
        apiKey.setTenantId(tenantId);
        apiKey.setKeyName("Test API Key");
        apiKey.setStatus(ApiKeyStatus.ACTIVE);

        // Initialize Rate Limit test data
        rateLimitDto = new RateLimitDto();
        rateLimitDto.setId(testId);
        rateLimitDto.setTenantId(tenantId);
        rateLimitDto.setLimitName("Test Rate Limit");
        rateLimitDto.setRequestsPerWindow(100);
        rateLimitDto.setWindowSizeSeconds(60);

        rateLimit = new RateLimit();
        rateLimit.setId(testId);
        rateLimit.setTenantId(tenantId);
        rateLimit.setLimitName("Test Rate Limit");
        rateLimit.setRequestsPerWindow(100);
        rateLimit.setWindowSizeSeconds(60);

        // Initialize Request Log test data
        requestLogDto = new RequestLogDto();
        requestLogDto.setId(testId);
        requestLogDto.setTenantId(tenantId);
        requestLogDto.setRequestId(UUID.randomUUID().toString());
        requestLogDto.setServiceId("ai-inference");
        requestLogDto.setHttpMethod("POST");
        requestLogDto.setRequestPath("/api/v1/inference");
        requestLogDto.setStatusCode(200);

        requestLog = new RequestLog();
        requestLog.setId(testId);
        requestLog.setTenantId(tenantId);
        requestLog.setRequestId(UUID.randomUUID().toString());
        requestLog.setServiceId("ai-inference");
        requestLog.setHttpMethod("POST");
        requestLog.setRequestPath("/api/v1/inference");
        requestLog.setStatusCode(200);
    }

    // ============================================================
    // Gateway Config Operations Tests
    // ============================================================

    @Test
    @DisplayName("Should create gateway config successfully")
    void shouldCreateGatewayConfigSuccessfully() {
        // Given
        when(gatewayConfigMapper.toDomain(gatewayConfigDto)).thenReturn(gatewayConfig);
        when(gatewayConfigRepository.save(any(GatewayConfig.class))).thenReturn(gatewayConfig);
        when(gatewayConfigMapper.toDto(gatewayConfig)).thenReturn(gatewayConfigDto);

        // When
        GatewayConfigDto result = applicationService.createGatewayConfig(gatewayConfigDto);

        // Then
        assertNotNull(result);
        assertEquals(gatewayConfigDto.getTenantId(), result.getTenantId());
        assertEquals(gatewayConfigDto.getConfigName(), result.getConfigName());

        verify(gatewayConfigMapper).toDomain(gatewayConfigDto);
        verify(gatewayConfigRepository).save(any(GatewayConfig.class));
        verify(gatewayConfigMapper).toDto(gatewayConfig);
    }

    @Test
    @DisplayName("Should get gateway config by ID")
    void shouldGetGatewayConfigById() {
        // Given
        when(gatewayConfigRepository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(gatewayConfig));
        when(gatewayConfigMapper.toDto(gatewayConfig)).thenReturn(gatewayConfigDto);

        // When
        GatewayConfigDto result = applicationService.getGatewayConfig(testId, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(tenantId, result.getTenantId());

        verify(gatewayConfigRepository).findByIdAndTenantId(testId, tenantId);
        verify(gatewayConfigMapper).toDto(gatewayConfig);
    }

    @Test
    @DisplayName("Should throw exception when gateway config not found")
    void shouldThrowExceptionWhenGatewayConfigNotFound() {
        // Given
        when(gatewayConfigRepository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> applicationService.getGatewayConfig(testId, tenantId));

        verify(gatewayConfigRepository).findByIdAndTenantId(testId, tenantId);
        verify(gatewayConfigMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should get all gateway configs for tenant")
    void shouldGetAllGatewayConfigsForTenant() {
        // Given
        GatewayConfig config2 = new GatewayConfig();
        config2.setId(UUID.randomUUID());
        config2.setTenantId(tenantId);

        GatewayConfigDto configDto2 = new GatewayConfigDto();
        configDto2.setId(config2.getId());
        configDto2.setTenantId(tenantId);

        when(gatewayConfigRepository.findByTenantId(tenantId))
                .thenReturn(Arrays.asList(gatewayConfig, config2));
        when(gatewayConfigMapper.toDto(gatewayConfig)).thenReturn(gatewayConfigDto);
        when(gatewayConfigMapper.toDto(config2)).thenReturn(configDto2);

        // When
        List<GatewayConfigDto> result = applicationService.getAllGatewayConfigs(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(tenantId, result.get(0).getTenantId());
        assertEquals(tenantId, result.get(1).getTenantId());

        verify(gatewayConfigRepository).findByTenantId(tenantId);
        verify(gatewayConfigMapper, times(2)).toDto(any(GatewayConfig.class));
    }

    @Test
    @DisplayName("Should update gateway config successfully")
    void shouldUpdateGatewayConfigSuccessfully() {
        // Given
        GatewayConfigDto updateDto = new GatewayConfigDto();
        updateDto.setConfigName("Updated Config");
        updateDto.setDescription("Updated description");

        when(gatewayConfigRepository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(gatewayConfig));
        when(gatewayConfigRepository.save(any(GatewayConfig.class))).thenReturn(gatewayConfig);
        when(gatewayConfigMapper.toDto(gatewayConfig)).thenReturn(updateDto);

        // When
        GatewayConfigDto result = applicationService.updateGatewayConfig(testId, tenantId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Config", result.getConfigName());

        verify(gatewayConfigRepository).findByIdAndTenantId(testId, tenantId);
        verify(gatewayConfigRepository).save(any(GatewayConfig.class));
        verify(gatewayConfigMapper).toDto(gatewayConfig);
    }

    @Test
    @DisplayName("Should delete gateway config successfully")
    void shouldDeleteGatewayConfigSuccessfully() {
        // Given
        when(gatewayConfigRepository.existsByIdAndTenantId(testId, tenantId))
                .thenReturn(true);
        doNothing().when(gatewayConfigRepository).deleteByIdAndTenantId(testId, tenantId);

        // When
        applicationService.deleteGatewayConfig(testId, tenantId);

        // Then
        verify(gatewayConfigRepository).existsByIdAndTenantId(testId, tenantId);
        verify(gatewayConfigRepository).deleteByIdAndTenantId(testId, tenantId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent gateway config")
    void shouldThrowExceptionWhenDeletingNonExistentGatewayConfig() {
        // Given
        when(gatewayConfigRepository.existsByIdAndTenantId(testId, tenantId))
                .thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> applicationService.deleteGatewayConfig(testId, tenantId));

        verify(gatewayConfigRepository).existsByIdAndTenantId(testId, tenantId);
        verify(gatewayConfigRepository, never()).deleteByIdAndTenantId(any(), any());
    }

    // ============================================================
    // Route Operations Tests
    // ============================================================

    @Test
    @DisplayName("Should create route successfully")
    void shouldCreateRouteSuccessfully() {
        // Given
        when(routeMapper.toDomain(routeDto)).thenReturn(route);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toDto(route)).thenReturn(routeDto);

        // When
        RouteDto result = applicationService.createRoute(routeDto);

        // Then
        assertNotNull(result);
        assertEquals(routeDto.getRouteName(), result.getRouteName());

        verify(routeMapper).toDomain(routeDto);
        verify(routeRepository).save(any(Route.class));
        verify(routeMapper).toDto(route);
    }

    @Test
    @DisplayName("Should get active routes for tenant")
    void shouldGetActiveRoutesForTenant() {
        // Given
        Route inactiveRoute = new Route();
        inactiveRoute.setId(UUID.randomUUID());
        inactiveRoute.setTenantId(tenantId);
        inactiveRoute.setStatus(RouteStatus.INACTIVE);

        when(routeRepository.findByTenantIdAndStatus(tenantId, RouteStatus.ACTIVE))
                .thenReturn(Arrays.asList(route));
        when(routeMapper.toDto(route)).thenReturn(routeDto);

        // When
        List<RouteDto> result = applicationService.getActiveRoutes(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RouteStatus.ACTIVE.name(), result.get(0).getStatus());

        verify(routeRepository).findByTenantIdAndStatus(tenantId, RouteStatus.ACTIVE);
        verify(routeMapper).toDto(route);
    }

    @Test
    @DisplayName("Should update route successfully")
    void shouldUpdateRouteSuccessfully() {
        // Given
        RouteDto updateDto = new RouteDto();
        updateDto.setRouteName("Updated Route");
        updateDto.setPath("/api/updated");
        updateDto.setPriority(10);

        when(routeRepository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(route));
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toDto(route)).thenReturn(updateDto);

        // When
        RouteDto result = applicationService.updateRoute(testId, tenantId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Route", result.getRouteName());

        verify(routeRepository).findByIdAndTenantId(testId, tenantId);
        verify(routeRepository).save(any(Route.class));
    }

    @Test
    @DisplayName("Should delete route successfully")
    void shouldDeleteRouteSuccessfully() {
        // Given
        when(routeRepository.existsByIdAndTenantId(testId, tenantId))
                .thenReturn(true);
        doNothing().when(routeRepository).deleteByIdAndTenantId(testId, tenantId);

        // When
        applicationService.deleteRoute(testId, tenantId);

        // Then
        verify(routeRepository).existsByIdAndTenantId(testId, tenantId);
        verify(routeRepository).deleteByIdAndTenantId(testId, tenantId);
    }

    // ============================================================
    // API Key Operations Tests
    // ============================================================

    @Test
    @DisplayName("Should create API key successfully")
    void shouldCreateApiKeySuccessfully() {
        // Given
        when(apiKeyMapper.toDomain(apiKeyDto)).thenReturn(apiKey);
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(apiKey);
        when(apiKeyMapper.toDto(apiKey)).thenReturn(apiKeyDto);

        // When
        ApiKeyDto result = applicationService.createApiKey(apiKeyDto);

        // Then
        assertNotNull(result);
        assertEquals(apiKeyDto.getKeyName(), result.getKeyName());

        verify(apiKeyMapper).toDomain(apiKeyDto);
        verify(apiKeyRepository).save(any(ApiKey.class));
        verify(apiKeyMapper).toDto(apiKey);
    }

    @Test
    @DisplayName("Should create API key with auto-generated key")
    void shouldCreateApiKeyWithAutoGeneratedKey() {
        // Given
        apiKeyDto.setApiKey(null);
        apiKey.setApiKey(null);

        when(apiKeyMapper.toDomain(apiKeyDto)).thenReturn(apiKey);
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
            ApiKey savedKey = invocation.getArgument(0);
            if (savedKey.getApiKey() == null || savedKey.getApiKey().isEmpty()) {
                savedKey.setApiKey(UUID.randomUUID().toString().replace("-", ""));
            }
            return savedKey;
        });
        when(apiKeyMapper.toDto(any(ApiKey.class))).thenReturn(apiKeyDto);

        // When
        ApiKeyDto result = applicationService.createApiKey(apiKeyDto);

        // Then
        assertNotNull(result);
        verify(apiKeyMapper).toDomain(apiKeyDto);
        verify(apiKeyRepository).save(any(ApiKey.class));
    }

    @Test
    @DisplayName("Should revoke API key successfully")
    void shouldRevokeApiKeySuccessfully() {
        // Given
        when(apiKeyRepository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(apiKey));
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(apiKey);

        // When
        applicationService.revokeApiKey(testId, tenantId);

        // Then
        assertEquals(ApiKeyStatus.REVOKED, apiKey.getStatus());
        verify(apiKeyRepository).findByIdAndTenantId(testId, tenantId);
        verify(apiKeyRepository).save(any(ApiKey.class));
    }

    @Test
    @DisplayName("Should throw exception when revoking non-existent API key")
    void shouldThrowExceptionWhenRevokingNonExistentApiKey() {
        // Given
        when(apiKeyRepository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> applicationService.revokeApiKey(testId, tenantId));

        verify(apiKeyRepository).findByIdAndTenantId(testId, tenantId);
        verify(apiKeyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all API keys for tenant")
    void shouldGetAllApiKeysForTenant() {
        // Given
        ApiKey key2 = new ApiKey();
        key2.setId(UUID.randomUUID());
        key2.setTenantId(tenantId);

        when(apiKeyRepository.findByTenantId(tenantId))
                .thenReturn(Arrays.asList(apiKey, key2));
        when(apiKeyMapper.toDto(apiKey)).thenReturn(apiKeyDto);
        when(apiKeyMapper.toDto(key2)).thenReturn(apiKeyDto);

        // When
        List<ApiKeyDto> result = applicationService.getAllApiKeys(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(apiKeyRepository).findByTenantId(tenantId);
        verify(apiKeyMapper, times(2)).toDto(any(ApiKey.class));
    }

    // ============================================================
    // Rate Limit Operations Tests
    // ============================================================

    @Test
    @DisplayName("Should create rate limit successfully")
    void shouldCreateRateLimitSuccessfully() {
        // Given
        when(rateLimitMapper.toDomain(rateLimitDto)).thenReturn(rateLimit);
        when(rateLimitRepository.save(any(RateLimit.class))).thenReturn(rateLimit);
        when(rateLimitMapper.toDto(rateLimit)).thenReturn(rateLimitDto);

        // When
        RateLimitDto result = applicationService.createRateLimit(rateLimitDto);

        // Then
        assertNotNull(result);
        assertEquals(rateLimitDto.getLimitName(), result.getLimitName());

        verify(rateLimitMapper).toDomain(rateLimitDto);
        verify(rateLimitRepository).save(any(RateLimit.class));
        verify(rateLimitMapper).toDto(rateLimit);
    }

    @Test
    @DisplayName("Should get rate limit by ID")
    void shouldGetRateLimitById() {
        // Given
        when(rateLimitRepository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(rateLimit));
        when(rateLimitMapper.toDto(rateLimit)).thenReturn(rateLimitDto);

        // When
        RateLimitDto result = applicationService.getRateLimit(testId, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());

        verify(rateLimitRepository).findByIdAndTenantId(testId, tenantId);
        verify(rateLimitMapper).toDto(rateLimit);
    }

    @Test
    @DisplayName("Should update rate limit successfully")
    void shouldUpdateRateLimitSuccessfully() {
        // Given
        RateLimitDto updateDto = new RateLimitDto();
        updateDto.setLimitName("Updated Rate Limit");
        updateDto.setRequestsPerWindow(200);

        when(rateLimitRepository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(rateLimit));
        when(rateLimitRepository.save(any(RateLimit.class))).thenReturn(rateLimit);
        when(rateLimitMapper.toDto(rateLimit)).thenReturn(updateDto);

        // When
        RateLimitDto result = applicationService.updateRateLimit(testId, tenantId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Rate Limit", result.getLimitName());

        verify(rateLimitRepository).findByIdAndTenantId(testId, tenantId);
        verify(rateLimitRepository).save(any(RateLimit.class));
    }

    @Test
    @DisplayName("Should delete rate limit successfully")
    void shouldDeleteRateLimitSuccessfully() {
        // Given
        when(rateLimitRepository.existsByIdAndTenantId(testId, tenantId))
                .thenReturn(true);
        doNothing().when(rateLimitRepository).deleteByIdAndTenantId(testId, tenantId);

        // When
        applicationService.deleteRateLimit(testId, tenantId);

        // Then
        verify(rateLimitRepository).existsByIdAndTenantId(testId, tenantId);
        verify(rateLimitRepository).deleteByIdAndTenantId(testId, tenantId);
    }

    // ============================================================
    // Request Log Operations Tests
    // ============================================================

    @Test
    @DisplayName("Should log request successfully")
    void shouldLogRequestSuccessfully() {
        // Given
        when(requestLogMapper.toDomain(requestLogDto)).thenReturn(requestLog);
        when(requestLogRepository.save(any(RequestLog.class))).thenReturn(requestLog);
        when(requestLogMapper.toDto(requestLog)).thenReturn(requestLogDto);

        // When
        RequestLogDto result = applicationService.logRequest(requestLogDto);

        // Then
        assertNotNull(result);
        assertEquals(requestLogDto.getRequestId(), result.getRequestId());

        verify(requestLogMapper).toDomain(requestLogDto);
        verify(requestLogRepository).save(any(RequestLog.class));
        verify(requestLogMapper).toDto(requestLog);
    }

    @Test
    @DisplayName("Should get request logs with limit")
    void shouldGetRequestLogsWithLimit() {
        // Given
        RequestLog log2 = new RequestLog();
        log2.setId(UUID.randomUUID());
        log2.setTenantId(tenantId);

        when(requestLogRepository.findByTenantId(tenantId))
                .thenReturn(Arrays.asList(requestLog, log2));
        when(requestLogMapper.toDto(requestLog)).thenReturn(requestLogDto);
        when(requestLogMapper.toDto(log2)).thenReturn(requestLogDto);

        // When
        List<RequestLogDto> result = applicationService.getRequestLogs(tenantId, 10);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(requestLogRepository).findByTenantId(tenantId);
        verify(requestLogMapper, times(2)).toDto(any(RequestLog.class));
    }

    @Test
    @DisplayName("Should get request logs by service")
    void shouldGetRequestLogsByService() {
        // Given
        String serviceId = "ai-inference";
        when(requestLogRepository.findByTenantIdAndServiceId(tenantId, serviceId))
                .thenReturn(Arrays.asList(requestLog));
        when(requestLogMapper.toDto(requestLog)).thenReturn(requestLogDto);

        // When
        List<RequestLogDto> result = applicationService.getRequestLogsByService(tenantId, serviceId, 100);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(requestLogRepository).findByTenantIdAndServiceId(tenantId, serviceId);
        verify(requestLogMapper).toDto(requestLog);
    }

    @Test
    @DisplayName("Should cleanup old logs successfully")
    void shouldCleanupOldLogsSuccessfully() {
        // Given
        int daysToKeep = 30;
        doNothing().when(requestLogRepository).deleteByTenantIdAndCreatedAtBefore(eq(tenantId), any(LocalDateTime.class));

        // When
        applicationService.cleanupOldLogs(tenantId, daysToKeep);

        // Then
        verify(requestLogRepository).deleteByTenantIdAndCreatedAtBefore(eq(tenantId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should get all rate limits for tenant")
    void shouldGetAllRateLimitsForTenant() {
        // Given
        RateLimit limit2 = new RateLimit();
        limit2.setId(UUID.randomUUID());
        limit2.setTenantId(tenantId);

        when(rateLimitRepository.findByTenantId(tenantId))
                .thenReturn(Arrays.asList(rateLimit, limit2));
        when(rateLimitMapper.toDto(rateLimit)).thenReturn(rateLimitDto);
        when(rateLimitMapper.toDto(limit2)).thenReturn(rateLimitDto);

        // When
        List<RateLimitDto> result = applicationService.getAllRateLimits(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(rateLimitRepository).findByTenantId(tenantId);
        verify(rateLimitMapper, times(2)).toDto(any(RateLimit.class));
    }

    @Test
    @DisplayName("Should get all routes for tenant")
    void shouldGetAllRoutesForTenant() {
        // Given
        Route route2 = new Route();
        route2.setId(UUID.randomUUID());
        route2.setTenantId(tenantId);

        when(routeRepository.findByTenantId(tenantId))
                .thenReturn(Arrays.asList(route, route2));
        when(routeMapper.toDto(route)).thenReturn(routeDto);
        when(routeMapper.toDto(route2)).thenReturn(routeDto);

        // When
        List<RouteDto> result = applicationService.getAllRoutes(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(routeRepository).findByTenantId(tenantId);
        verify(routeMapper, times(2)).toDto(any(Route.class));
    }
}
