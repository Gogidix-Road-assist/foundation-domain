package com.gogidix.rapidassist.ai.gateway.interfaces.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.gateway.application.dto.*;
import com.gogidix.rapidassist.ai.gateway.application.service.GatewayApplicationService;
import com.gogidix.rapidassist.ai.gateway.bootstrap.Application;
import com.gogidix.rapidassist.ai.gateway.domain.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for GatewayRestController.
 * Tests REST API endpoints, request handling, and responses.
 */
@WebMvcTest(controllers = GatewayRestController.class)
@ContextConfiguration(classes = {Application.class, GatewayRestController.class})
@DisplayName("Gateway REST Controller Tests")
class GatewayRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GatewayApplicationService applicationService;

    private final String tenantId = "tenant-123";
    private final UUID testId = UUID.randomUUID();

    private GatewayConfigDto gatewayConfigDto;
    private RouteDto routeDto;
    private ApiKeyDto apiKeyDto;
    private RateLimitDto rateLimitDto;
    private RequestLogDto requestLogDto;

    @BeforeEach
    void setUp() {
        // Setup TenantContext
        TenantContext.setTenantId(tenantId);

        // Initialize Gateway Config DTO
        gatewayConfigDto = new GatewayConfigDto();
        gatewayConfigDto.setId(testId);
        gatewayConfigDto.setTenantId(tenantId);
        gatewayConfigDto.setConfigName("Test Config");
        gatewayConfigDto.setDescription("Test description");

        // Initialize Route DTO
        routeDto = new RouteDto();
        routeDto.setId(testId);
        routeDto.setTenantId(tenantId);
        routeDto.setRouteName("Test Route");
        routeDto.setPath("/api/test");
        routeDto.setServiceUrl("http://localhost:8080");
        routeDto.setHttpMethod("GET");
        routeDto.setStatus("ACTIVE");

        // Initialize API Key DTO
        apiKeyDto = new ApiKeyDto();
        apiKeyDto.setId(testId);
        apiKeyDto.setTenantId(tenantId);
        apiKeyDto.setKeyName("Test API Key");
        apiKeyDto.setStatus("ACTIVE");
        apiKeyDto.setApiKey("test-api-key-123");

        // Initialize Rate Limit DTO
        rateLimitDto = new RateLimitDto();
        rateLimitDto.setId(testId);
        rateLimitDto.setTenantId(tenantId);
        rateLimitDto.setLimitName("Test Rate Limit");
        rateLimitDto.setRequestsPerWindow(100);
        rateLimitDto.setWindowSizeSeconds(60);

        // Initialize Request Log DTO
        requestLogDto = new RequestLogDto();
        requestLogDto.setId(testId);
        requestLogDto.setTenantId(tenantId);
        requestLogDto.setRequestId(UUID.randomUUID().toString());
        requestLogDto.setServiceId("ai-inference");
        requestLogDto.setHttpMethod("POST");
        requestLogDto.setRequestPath("/api/v1/inference");
        requestLogDto.setStatusCode(200);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    // ============================================================
    // Gateway Config Endpoints Tests
    // ============================================================

    @Test
    @DisplayName("POST /configs - Should create gateway config")
    void shouldCreateGatewayConfig() throws Exception {
        // Given
        when(applicationService.createGatewayConfig(any(GatewayConfigDto.class)))
                .thenReturn(gatewayConfigDto);

        // When & Then
        mockMvc.perform(post("/api/v1/gateway/configs")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gatewayConfigDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.configName").value("Test Config"));

        verify(applicationService).createGatewayConfig(any(GatewayConfigDto.class));
    }

    @Test
    @DisplayName("GET /configs/{configId} - Should get gateway config by ID")
    void shouldGetGatewayConfigById() throws Exception {
        // Given
        when(applicationService.getGatewayConfig(testId, tenantId))
                .thenReturn(gatewayConfigDto);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/configs/{configId}", testId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.configName").value("Test Config"));

        verify(applicationService).getGatewayConfig(testId, tenantId);
    }

    @Test
    @DisplayName("GET /configs - Should get all gateway configs")
    void shouldGetAllGatewayConfigs() throws Exception {
        // Given
        GatewayConfigDto config2 = new GatewayConfigDto();
        config2.setId(UUID.randomUUID());
        config2.setTenantId(tenantId);
        config2.setConfigName("Config 2");

        List<GatewayConfigDto> configs = Arrays.asList(gatewayConfigDto, config2);
        when(applicationService.getAllGatewayConfigs(tenantId)).thenReturn(configs);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/configs")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].configName").value("Test Config"))
                .andExpect(jsonPath("$[1].configName").value("Config 2"));

        verify(applicationService).getAllGatewayConfigs(tenantId);
    }

    @Test
    @DisplayName("PUT /configs/{configId} - Should update gateway config")
    void shouldUpdateGatewayConfig() throws Exception {
        // Given
        GatewayConfigDto updateDto = new GatewayConfigDto();
        updateDto.setConfigName("Updated Config");
        updateDto.setDescription("Updated description");

        GatewayConfigDto updatedDto = new GatewayConfigDto();
        updatedDto.setId(testId);
        updatedDto.setConfigName("Updated Config");
        updatedDto.setDescription("Updated description");

        when(applicationService.updateGatewayConfig(eq(testId), eq(tenantId), any(GatewayConfigDto.class)))
                .thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/gateway/configs/{configId}", testId)
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configName").value("Updated Config"));

        verify(applicationService).updateGatewayConfig(eq(testId), eq(tenantId), any(GatewayConfigDto.class));
    }

    @Test
    @DisplayName("DELETE /configs/{configId} - Should delete gateway config")
    void shouldDeleteGatewayConfig() throws Exception {
        // Given
        doNothing().when(applicationService).deleteGatewayConfig(testId, tenantId);

        // When & Then
        mockMvc.perform(delete("/api/v1/gateway/configs/{configId}", testId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());

        verify(applicationService).deleteGatewayConfig(testId, tenantId);
    }

    // ============================================================
    // Route Endpoints Tests
    // ============================================================

    @Test
    @DisplayName("POST /routes - Should create route")
    void shouldCreateRoute() throws Exception {
        // Given
        when(applicationService.createRoute(any(RouteDto.class)))
                .thenReturn(routeDto);

        // When & Then
        mockMvc.perform(post("/api/v1/gateway/routes")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.routeName").value("Test Route"))
                .andExpect(jsonPath("$.path").value("/api/test"));

        verify(applicationService).createRoute(any(RouteDto.class));
    }

    @Test
    @DisplayName("GET /routes/{routeId} - Should get route by ID")
    void shouldGetRouteById() throws Exception {
        // Given
        when(applicationService.getRoute(testId, tenantId))
                .thenReturn(routeDto);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/routes/{routeId}", testId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.routeName").value("Test Route"));

        verify(applicationService).getRoute(testId, tenantId);
    }

    @Test
    @DisplayName("GET /routes - Should get all routes")
    void shouldGetAllRoutes() throws Exception {
        // Given
        RouteDto route2 = new RouteDto();
        route2.setId(UUID.randomUUID());
        route2.setRouteName("Route 2");

        List<RouteDto> routes = Arrays.asList(routeDto, route2);
        when(applicationService.getAllRoutes(tenantId)).thenReturn(routes);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/routes")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(applicationService).getAllRoutes(tenantId);
    }

    @Test
    @DisplayName("GET /routes/active - Should get active routes")
    void shouldGetActiveRoutes() throws Exception {
        // Given
        RouteDto activeRoute2 = new RouteDto();
        activeRoute2.setId(UUID.randomUUID());
        activeRoute2.setRouteName("Active Route 2");
        activeRoute2.setStatus("ACTIVE");

        List<RouteDto> activeRoutes = Arrays.asList(routeDto, activeRoute2);
        when(applicationService.getActiveRoutes(tenantId)).thenReturn(activeRoutes);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/routes/active")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(applicationService).getActiveRoutes(tenantId);
    }

    @Test
    @DisplayName("PUT /routes/{routeId} - Should update route")
    void shouldUpdateRoute() throws Exception {
        // Given
        RouteDto updateDto = new RouteDto();
        updateDto.setRouteName("Updated Route");
        updateDto.setPath("/api/updated");

        RouteDto updatedDto = new RouteDto();
        updatedDto.setId(testId);
        updatedDto.setRouteName("Updated Route");
        updatedDto.setPath("/api/updated");

        when(applicationService.updateRoute(eq(testId), eq(tenantId), any(RouteDto.class)))
                .thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/gateway/routes/{routeId}", testId)
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeName").value("Updated Route"));

        verify(applicationService).updateRoute(eq(testId), eq(tenantId), any(RouteDto.class));
    }

    @Test
    @DisplayName("DELETE /routes/{routeId} - Should delete route")
    void shouldDeleteRoute() throws Exception {
        // Given
        doNothing().when(applicationService).deleteRoute(testId, tenantId);

        // When & Then
        mockMvc.perform(delete("/api/v1/gateway/routes/{routeId}", testId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());

        verify(applicationService).deleteRoute(testId, tenantId);
    }

    // ============================================================
    // API Key Endpoints Tests
    // ============================================================

    @Test
    @DisplayName("POST /api-keys - Should create API key")
    void shouldCreateApiKey() throws Exception {
        // Given
        when(applicationService.createApiKey(any(ApiKeyDto.class)))
                .thenReturn(apiKeyDto);

        // When & Then
        mockMvc.perform(post("/api/v1/gateway/api-keys")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiKeyDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.keyName").value("Test API Key"));

        verify(applicationService).createApiKey(any(ApiKeyDto.class));
    }

    @Test
    @DisplayName("GET /api-keys/{keyId} - Should get API key by ID")
    void shouldGetApiKeyById() throws Exception {
        // Given
        when(applicationService.getApiKey(testId, tenantId))
                .thenReturn(apiKeyDto);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/api-keys/{keyId}", testId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.keyName").value("Test API Key"));

        verify(applicationService).getApiKey(testId, tenantId);
    }

    @Test
    @DisplayName("GET /api-keys - Should get all API keys")
    void shouldGetAllApiKeys() throws Exception {
        // Given
        ApiKeyDto key2 = new ApiKeyDto();
        key2.setId(UUID.randomUUID());
        key2.setKeyName("Key 2");

        List<ApiKeyDto> keys = Arrays.asList(apiKeyDto, key2);
        when(applicationService.getAllApiKeys(tenantId)).thenReturn(keys);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/api-keys")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(applicationService).getAllApiKeys(tenantId);
    }

    @Test
    @DisplayName("PUT /api-keys/{keyId} - Should update API key")
    void shouldUpdateApiKey() throws Exception {
        // Given
        ApiKeyDto updateDto = new ApiKeyDto();
        updateDto.setKeyName("Updated Key");

        ApiKeyDto updatedDto = new ApiKeyDto();
        updatedDto.setId(testId);
        updatedDto.setKeyName("Updated Key");

        when(applicationService.updateApiKey(eq(testId), eq(tenantId), any(ApiKeyDto.class)))
                .thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/gateway/api-keys/{keyId}", testId)
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keyName").value("Updated Key"));

        verify(applicationService).updateApiKey(eq(testId), eq(tenantId), any(ApiKeyDto.class));
    }

    @Test
    @DisplayName("DELETE /api-keys/{keyId} - Should delete API key")
    void shouldDeleteApiKey() throws Exception {
        // Given
        doNothing().when(applicationService).deleteApiKey(testId, tenantId);

        // When & Then
        mockMvc.perform(delete("/api/v1/gateway/api-keys/{keyId}", testId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());

        verify(applicationService).deleteApiKey(testId, tenantId);
    }

    @Test
    @DisplayName("POST /api-keys/{keyId}/revoke - Should revoke API key")
    void shouldRevokeApiKey() throws Exception {
        // Given
        doNothing().when(applicationService).revokeApiKey(testId, tenantId);

        // When & Then
        mockMvc.perform(post("/api/v1/gateway/api-keys/{keyId}/revoke", testId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());

        verify(applicationService).revokeApiKey(testId, tenantId);
    }

    // ============================================================
    // Rate Limit Endpoints Tests
    // ============================================================

    @Test
    @DisplayName("POST /rate-limits - Should create rate limit")
    void shouldCreateRateLimit() throws Exception {
        // Given
        when(applicationService.createRateLimit(any(RateLimitDto.class)))
                .thenReturn(rateLimitDto);

        // When & Then
        mockMvc.perform(post("/api/v1/gateway/rate-limits")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateLimitDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.limitName").value("Test Rate Limit"));

        verify(applicationService).createRateLimit(any(RateLimitDto.class));
    }

    @Test
    @DisplayName("GET /rate-limits/{limitId} - Should get rate limit by ID")
    void shouldGetRateLimitById() throws Exception {
        // Given
        when(applicationService.getRateLimit(testId, tenantId))
                .thenReturn(rateLimitDto);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/rate-limits/{limitId}", testId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.limitName").value("Test Rate Limit"));

        verify(applicationService).getRateLimit(testId, tenantId);
    }

    @Test
    @DisplayName("GET /rate-limits - Should get all rate limits")
    void shouldGetAllRateLimits() throws Exception {
        // Given
        RateLimitDto limit2 = new RateLimitDto();
        limit2.setId(UUID.randomUUID());
        limit2.setLimitName("Limit 2");

        List<RateLimitDto> limits = Arrays.asList(rateLimitDto, limit2);
        when(applicationService.getAllRateLimits(tenantId)).thenReturn(limits);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/rate-limits")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(applicationService).getAllRateLimits(tenantId);
    }

    @Test
    @DisplayName("PUT /rate-limits/{limitId} - Should update rate limit")
    void shouldUpdateRateLimit() throws Exception {
        // Given
        RateLimitDto updateDto = new RateLimitDto();
        updateDto.setLimitName("Updated Rate Limit");

        RateLimitDto updatedDto = new RateLimitDto();
        updatedDto.setId(testId);
        updatedDto.setLimitName("Updated Rate Limit");

        when(applicationService.updateRateLimit(eq(testId), eq(tenantId), any(RateLimitDto.class)))
                .thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/gateway/rate-limits/{limitId}", testId)
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limitName").value("Updated Rate Limit"));

        verify(applicationService).updateRateLimit(eq(testId), eq(tenantId), any(RateLimitDto.class));
    }

    @Test
    @DisplayName("DELETE /rate-limits/{limitId} - Should delete rate limit")
    void shouldDeleteRateLimit() throws Exception {
        // Given
        doNothing().when(applicationService).deleteRateLimit(testId, tenantId);

        // When & Then
        mockMvc.perform(delete("/api/v1/gateway/rate-limits/{limitId}", testId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());

        verify(applicationService).deleteRateLimit(testId, tenantId);
    }

    // ============================================================
    // Request Log Endpoints Tests
    // ============================================================

    @Test
    @DisplayName("POST /logs - Should log request")
    void shouldLogRequest() throws Exception {
        // Given
        when(applicationService.logRequest(any(RequestLogDto.class)))
                .thenReturn(requestLogDto);

        // When & Then
        mockMvc.perform(post("/api/v1/gateway/logs")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLogDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.serviceId").value("ai-inference"));

        verify(applicationService).logRequest(any(RequestLogDto.class));
    }

    @Test
    @DisplayName("GET /logs - Should get request logs with limit")
    void shouldGetRequestLogsWithLimit() throws Exception {
        // Given
        RequestLogDto log2 = new RequestLogDto();
        log2.setId(UUID.randomUUID());
        log2.setServiceId("ai-nlp");

        List<RequestLogDto> logs = Arrays.asList(requestLogDto, log2);
        when(applicationService.getRequestLogs(tenantId, 100)).thenReturn(logs);

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/logs")
                        .header("X-Tenant-ID", tenantId)
                        .param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(applicationService).getRequestLogs(tenantId, 100);
    }

    @Test
    @DisplayName("GET /logs - Should get request logs with default limit")
    void shouldGetRequestLogsWithDefaultLimit() throws Exception {
        // Given
        when(applicationService.getRequestLogs(tenantId, 100))
                .thenReturn(Arrays.asList(requestLogDto));

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/logs")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(applicationService).getRequestLogs(tenantId, 100);
    }

    @Test
    @DisplayName("GET /logs/service/{serviceId} - Should get logs by service")
    void shouldGetRequestLogsByService() throws Exception {
        // Given
        String serviceId = "ai-inference";
        when(applicationService.getRequestLogsByService(tenantId, serviceId, 100))
                .thenReturn(Arrays.asList(requestLogDto));

        // When & Then
        mockMvc.perform(get("/api/v1/gateway/logs/service/{serviceId}", serviceId)
                        .header("X-Tenant-ID", tenantId)
                        .param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].serviceId").value(serviceId));

        verify(applicationService).getRequestLogsByService(tenantId, serviceId, 100);
    }

    @Test
    @DisplayName("DELETE /logs/cleanup - Should cleanup old logs")
    void shouldCleanupOldLogs() throws Exception {
        // Given
        doNothing().when(applicationService).cleanupOldLogs(tenantId, 30);

        // When & Then
        mockMvc.perform(delete("/api/v1/gateway/logs/cleanup")
                        .header("X-Tenant-ID", tenantId)
                        .param("daysToKeep", "30"))
                .andExpect(status().isNoContent());

        verify(applicationService).cleanupOldLogs(tenantId, 30);
    }

    @Test
    @DisplayName("DELETE /logs/cleanup - Should cleanup old logs with default days")
    void shouldCleanupOldLogsWithDefaultDays() throws Exception {
        // Given
        doNothing().when(applicationService).cleanupOldLogs(tenantId, 30);

        // When & Then
        mockMvc.perform(delete("/api/v1/gateway/logs/cleanup")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());

        verify(applicationService).cleanupOldLogs(tenantId, 30);
    }
}
