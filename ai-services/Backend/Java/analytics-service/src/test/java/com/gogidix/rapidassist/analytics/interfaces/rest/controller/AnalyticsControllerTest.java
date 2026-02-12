package com.gogidix.rapidassist.analytics.interfaces.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.analytics.application.dto.AnalyticsDto;
import com.gogidix.rapidassist.analytics.application.service.AnalyticsApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive unit tests for AnalyticsController.
 * Tests all REST endpoints with mocked application service.
 */
@WebMvcTest(AnalyticsController.class)
@DisplayName("Analytics REST Controller Tests")
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnalyticsApplicationService applicationService;

    private AnalyticsDto testDto;
    private String tenantId;
    private UUID analyticsId;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-123";
        analyticsId = UUID.randomUUID();

        testDto = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .dataSource("DATABASE")
                .metrics(Map.of("views", 1000, "clicks", 500))
                .dimensions(Map.of("region", "US"))
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .status("PENDING")
                .totalRecords(1500)
                .aggregationType("SUM")
                .computedBy("system")
                .description("Test analytics")
                .metadata(Map.of("version", "1.0"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create analytics and return 201 CREATED")
    void shouldCreateAnalyticsAndReturn201Created() throws Exception {
        // Given
        when(applicationService.createAnalytics(eq(tenantId), any(AnalyticsDto.class))).thenReturn(testDto);

        // When & Then
        mockMvc.perform(post("/api/v1/analytics")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(analyticsId.toString()))
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.analyticsType").value("USER_ENGAGEMENT"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(applicationService, times(1)).createAnalytics(eq(tenantId), any(AnalyticsDto.class));
    }

    @Test
    @DisplayName("Should get analytics by id and return 200 OK")
    void shouldGetAnalyticsByIdAndReturn200Ok() throws Exception {
        // Given
        when(applicationService.getAnalytics(tenantId, analyticsId)).thenReturn(testDto);

        // When & Then
        mockMvc.perform(get("/api/v1/analytics/{id}", analyticsId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(analyticsId.toString()))
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.analyticsType").value("USER_ENGAGEMENT"));

        verify(applicationService, times(1)).getAnalytics(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when getting non-existent analytics")
    void shouldReturn404WhenGettingNonExistentAnalytics() throws Exception {
        // Given
        when(applicationService.getAnalytics(tenantId, analyticsId))
                .thenThrow(new IllegalArgumentException("Analytics not found: " + analyticsId));

        // When & Then
        mockMvc.perform(get("/api/v1/analytics/{id}", analyticsId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNotFound());

        verify(applicationService, times(1)).getAnalytics(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should get all analytics by tenant and return 200 OK")
    void shouldGetAllAnalyticsByTenantAndReturn200Ok() throws Exception {
        // Given
        List<AnalyticsDto> analyticsList = List.of(testDto);
        when(applicationService.getAnalyticsByTenant(tenantId)).thenReturn(analyticsList);

        // When & Then
        mockMvc.perform(get("/api/v1/analytics")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(analyticsId.toString()))
                .andExpect(jsonPath("$[0].tenantId").value(tenantId));

        verify(applicationService, times(1)).getAnalyticsByTenant(tenantId);
    }

    @Test
    @DisplayName("Should return empty array when no analytics found for tenant")
    void shouldReturnEmptyArrayWhenNoAnalyticsFound() throws Exception {
        // Given
        when(applicationService.getAnalyticsByTenant(tenantId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/analytics")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(applicationService, times(1)).getAnalyticsByTenant(tenantId);
    }

    @Test
    @DisplayName("Should get analytics by status and return 200 OK")
    void shouldGetAnalyticsByStatusAndReturn200Ok() throws Exception {
        // Given
        String status = "PENDING";
        List<AnalyticsDto> analyticsList = List.of(testDto);
        when(applicationService.getAnalyticsByStatus(tenantId, status)).thenReturn(analyticsList);

        // When & Then
        mockMvc.perform(get("/api/v1/analytics/status/{status}", status)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(status));

        verify(applicationService, times(1)).getAnalyticsByStatus(tenantId, status);
    }

    @Test
    @DisplayName("Should get analytics by type and return 200 OK")
    void shouldGetAnalyticsByTypeAndReturn200Ok() throws Exception {
        // Given
        String analyticsType = "USER_ENGAGEMENT";
        List<AnalyticsDto> analyticsList = List.of(testDto);
        when(applicationService.getAnalyticsByType(tenantId, analyticsType)).thenReturn(analyticsList);

        // When & Then
        mockMvc.perform(get("/api/v1/analytics/type/{analyticsType}", analyticsType)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].analyticsType").value(analyticsType));

        verify(applicationService, times(1)).getAnalyticsByType(tenantId, analyticsType);
    }

    @Test
    @DisplayName("Should get analytics by time range and return 200 OK")
    void shouldGetAnalyticsByTimeRangeAndReturn200Ok() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        List<AnalyticsDto> analyticsList = List.of(testDto);
        when(applicationService.getAnalyticsByTimeRange(tenantId, startTime, endTime)).thenReturn(analyticsList);

        // When & Then
        mockMvc.perform(get("/api/v1/analytics/timerange")
                        .header("X-Tenant-ID", tenantId)
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService, times(1)).getAnalyticsByTimeRange(tenantId, startTime, endTime);
    }

    @Test
    @DisplayName("Should compute analytics and return 200 OK")
    void shouldComputeAnalyticsAndReturn200Ok() throws Exception {
        // Given
        AnalyticsDto computedDto = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .status("COMPLETED")
                .aggregationValue(1000.0)
                .computedAt(LocalDateTime.now())
                .build();

        when(applicationService.computeAnalytics(tenantId, analyticsId)).thenReturn(computedDto);

        // When & Then
        mockMvc.perform(post("/api/v1/analytics/{id}/compute", analyticsId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(analyticsId.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.aggregationValue").value(1000.0))
                .andExpect(jsonPath("$.computedAt").exists());

        verify(applicationService, times(1)).computeAnalytics(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when computing analytics not ready")
    void shouldReturn400WhenComputingAnalyticsNotReady() throws Exception {
        // Given
        when(applicationService.computeAnalytics(tenantId, analyticsId))
                .thenThrow(new IllegalStateException("Analytics is not ready for computation"));

        // When & Then
        mockMvc.perform(post("/api/v1/analytics/{id}/compute", analyticsId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isBadRequest());

        verify(applicationService, times(1)).computeAnalytics(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should delete analytics and return 204 NO CONTENT")
    void shouldDeleteAnalyticsAndReturn204NoContent() throws Exception {
        // Given
        doNothing().when(applicationService).deleteAnalytics(tenantId, analyticsId);

        // When & Then
        mockMvc.perform(delete("/api/v1/analytics/{id}", analyticsId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());

        verify(applicationService, times(1)).deleteAnalytics(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when deleting non-existent analytics")
    void shouldReturn404WhenDeletingNonExistentAnalytics() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Analytics not found: " + analyticsId))
                .when(applicationService).deleteAnalytics(tenantId, analyticsId);

        // When & Then
        mockMvc.perform(delete("/api/v1/analytics/{id}", analyticsId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNotFound());

        verify(applicationService, times(1)).deleteAnalytics(tenantId, analyticsId);
    }

    @Test
    @DisplayName("Should handle multiple analytics in list")
    void shouldHandleMultipleAnalyticsInList() throws Exception {
        // Given
        AnalyticsDto dto1 = AnalyticsDto.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_1")
                .build();
        AnalyticsDto dto2 = AnalyticsDto.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_2")
                .build();
        AnalyticsDto dto3 = AnalyticsDto.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_3")
                .build();

        List<AnalyticsDto> analyticsList = List.of(dto1, dto2, dto3);
        when(applicationService.getAnalyticsByTenant(tenantId)).thenReturn(analyticsList);

        // When & Then
        mockMvc.perform(get("/api/v1/analytics")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].analyticsType").value("TYPE_1"))
                .andExpect(jsonPath("$[1].analyticsType").value("TYPE_2"))
                .andExpect(jsonPath("$[2].analyticsType").value("TYPE_3"));

        verify(applicationService, times(1)).getAnalyticsByTenant(tenantId);
    }

    @Test
    @DisplayName("Should handle analytics with complex metrics")
    void shouldHandleAnalyticsWithComplexMetrics() throws Exception {
        // Given
        AnalyticsDto complexDto = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .metrics(Map.of(
                        "views", 10000,
                        "clicks", 5000,
                        "conversions", 250,
                        "revenue", 50000.0,
                        "ctr", 0.5,
                        "cr", 0.05
                ))
                .build();

        when(applicationService.createAnalytics(eq(tenantId), any(AnalyticsDto.class))).thenReturn(complexDto);

        // When & Then
        mockMvc.perform(post("/api/v1/analytics")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(complexDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metrics.views").value(10000))
                .andExpect(jsonPath("$.metrics.clicks").value(5000))
                .andExpect(jsonPath("$.metrics.conversions").value(250))
                .andExpect(jsonPath("$.metrics.revenue").value(50000.0))
                .andExpect(jsonPath("$.metrics.ctr").value(0.5))
                .andExpect(jsonPath("$.metrics.cr").value(0.05));

        verify(applicationService, times(1)).createAnalytics(eq(tenantId), any(AnalyticsDto.class));
    }

    @Test
    @DisplayName("Should handle analytics with complex dimensions")
    void shouldHandleAnalyticsWithComplexDimensions() throws Exception {
        // Given
        AnalyticsDto complexDto = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .dimensions(Map.of(
                        "region", "US",
                        "country", "United States",
                        "city", "New York",
                        "device", "mobile",
                        "browser", "chrome",
                        "os", "android",
                        "user_segment", "premium"
                ))
                .build();

        when(applicationService.createAnalytics(eq(tenantId), any(AnalyticsDto.class))).thenReturn(complexDto);

        // When & Then
        mockMvc.perform(post("/api/v1/analytics")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(complexDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dimensions.region").value("US"))
                .andExpect(jsonPath("$.dimensions.country").value("United States"))
                .andExpect(jsonPath("$.dimensions.device").value("mobile"))
                .andExpect(jsonPath("$.dimensions.user_segment").value("premium"));

        verify(applicationService, times(1)).createAnalytics(eq(tenantId), any(AnalyticsDto.class));
    }

    @Test
    @DisplayName("Should handle analytics with null metadata")
    void shouldHandleAnalyticsWithNullMetadata() throws Exception {
        // Given
        AnalyticsDto dtoWithNullMetadata = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .metadata(null)
                .build();

        when(applicationService.createAnalytics(eq(tenantId), any(AnalyticsDto.class))).thenReturn(dtoWithNullMetadata);

        // When & Then
        mockMvc.perform(post("/api/v1/analytics")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoWithNullMetadata)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metadata").isEmpty());

        verify(applicationService, times(1)).createAnalytics(eq(tenantId), any(AnalyticsDto.class));
    }

    @Test
    @DisplayName("Should handle different statuses in status endpoint")
    void shouldHandleDifferentStatusesInStatusEndpoint() throws Exception {
        // Given
        String pendingStatus = "PENDING";
        String completedStatus = "COMPLETED";
        String failedStatus = "FAILED";

        AnalyticsDto pendingDto = AnalyticsDto.builder().status(pendingStatus).build();
        AnalyticsDto completedDto = AnalyticsDto.builder().status(completedStatus).build();
        AnalyticsDto failedDto = AnalyticsDto.builder().status(failedStatus).build();

        when(applicationService.getAnalyticsByStatus(tenantId, pendingStatus)).thenReturn(List.of(pendingDto));
        when(applicationService.getAnalyticsByStatus(tenantId, completedStatus)).thenReturn(List.of(completedDto));
        when(applicationService.getAnalyticsByStatus(tenantId, failedStatus)).thenReturn(List.of(failedDto));

        // When & Then
        mockMvc.perform(get("/api/v1/analytics/status/{status}", pendingStatus)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(pendingStatus));

        mockMvc.perform(get("/api/v1/analytics/status/{status}", completedStatus)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(completedStatus));

        mockMvc.perform(get("/api/v1/analytics/status/{status}", failedStatus)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(failedStatus));

        verify(applicationService, times(1)).getAnalyticsByStatus(tenantId, pendingStatus);
        verify(applicationService, times(1)).getAnalyticsByStatus(tenantId, completedStatus);
        verify(applicationService, times(1)).getAnalyticsByStatus(tenantId, failedStatus);
    }

    @Test
    @DisplayName("Should handle time range with various date formats")
    void shouldHandleTimeRangeWithVariousDateFormats() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 31, 23, 59, 59);

        when(applicationService.getAnalyticsByTimeRange(eq(tenantId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(testDto));

        // When & Then
        mockMvc.perform(get("/api/v1/analytics/timerange")
                        .header("X-Tenant-ID", tenantId)
                        .param("startTime", "2024-01-01T00:00:00")
                        .param("endTime", "2024-01-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService, times(1)).getAnalyticsByTimeRange(eq(tenantId), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
