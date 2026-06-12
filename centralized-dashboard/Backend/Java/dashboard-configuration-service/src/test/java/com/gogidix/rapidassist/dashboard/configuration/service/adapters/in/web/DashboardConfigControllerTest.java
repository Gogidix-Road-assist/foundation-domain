package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto.*;
import com.gogidix.rapidassist.dashboard.configuration.service.application.DashboardConfigService;
import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DashboardConfigControllerTest {

    @Mock
    private DashboardConfigService dashboardService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private DashboardConfiguration testDashboard;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders
            .standaloneSetup(new DashboardConfigController(dashboardService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .setValidator(new LocalValidatorFactoryBean())
            .build();

        testDashboard = DashboardConfiguration.builder()
            .id("test-id")
            .tenantId("tenant-001")
            .dashboardId("dash-001")
            .name("Test Dashboard")
            .description("Test Description")
            .createdBy("user-123")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .version(1)
            .active(true)
            .widgets(Collections.emptyList())
            .build();
    }

    @Test
    void health_ShouldReturnStatusUp() throws Exception {
        mockMvc.perform(get("/api/dashboards/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.service").value("dashboard-configuration-service"));
    }

    @Test
    void getAllDashboards_ShouldReturnDashboardList() throws Exception {
        when(dashboardService.getAllDashboards())
            .thenReturn(CompletableFuture.completedFuture(List.of(testDashboard)));

        mockMvc.perform(get("/api/dashboards"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].dashboardId").value("dash-001"))
            .andExpect(jsonPath("$[0].name").value("Test Dashboard"));
    }

    @Test
    void getDashboard_WhenExists_ShouldReturnDashboard() throws Exception {
        when(dashboardService.getDashboard("dash-001"))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(testDashboard)));

        mockMvc.perform(get("/api/dashboards/dash-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dashboardId").value("dash-001"))
            .andExpect(jsonPath("$.name").value("Test Dashboard"));
    }

    @Test
    void getDashboard_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(dashboardService.getDashboard("non-existent"))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        mockMvc.perform(get("/api/dashboards/non-existent"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getDashboardsByTenant_ShouldReturnDashboardList() throws Exception {
        when(dashboardService.getDashboardsByTenant("tenant-001"))
            .thenReturn(CompletableFuture.completedFuture(List.of(testDashboard)));

        mockMvc.perform(get("/api/dashboards/tenant/tenant-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].tenantId").value("tenant-001"))
            .andExpect(jsonPath("$[0].dashboardId").value("dash-001"));
    }

    @Test
    void createDashboard_WithValidData_ShouldReturnCreated() throws Exception {
        CreateDashboardRequestDto request = new CreateDashboardRequestDto(
            "tenant-001",
            "dash-001",
            "Test Dashboard",
            "Test Description",
            "user-123"
        );

        when(dashboardService.createDashboard(
            eq("tenant-001"),
            eq("dash-001"),
            eq("Test Dashboard"),
            eq("Test Description"),
            eq("user-123")
        )).thenReturn(CompletableFuture.completedFuture(testDashboard));

        mockMvc.perform(post("/api/dashboards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dashboardId").value("dash-001"))
            .andExpect(jsonPath("$.name").value("Test Dashboard"));
    }

    @Test
    void createDashboard_WithMissingName_ShouldReturnBadRequest() throws Exception {
        CreateDashboardRequestDto request = new CreateDashboardRequestDto(
            "tenant-001",
            "dash-001",
            "",
            "Test Description",
            "user-123"
        );

        mockMvc.perform(post("/api/dashboards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createDashboard_WithShortDashboardId_ShouldReturnBadRequest() throws Exception {
        CreateDashboardRequestDto request = new CreateDashboardRequestDto(
            "tenant-001",
            "x",
            "Test Dashboard",
            "Test Description",
            "user-123"
        );

        mockMvc.perform(post("/api/dashboards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateDashboard_WithValidData_ShouldReturnUpdatedDashboard() throws Exception {
        UpdateDashboardRequestDto request = new UpdateDashboardRequestDto(
            "Updated Name",
            "Updated Description",
            "user-456"
        );

        DashboardConfiguration updatedDashboard = DashboardConfiguration.builder()
            .id(testDashboard.id())
            .tenantId(testDashboard.tenantId())
            .dashboardId(testDashboard.dashboardId())
            .name("Updated Name")
            .description("Updated Description")
            .createdBy(testDashboard.createdBy())
            .createdAt(testDashboard.createdAt())
            .updatedBy("user-456")
            .updatedAt(Instant.now())
            .version(testDashboard.version() + 1)
            .active(testDashboard.active())
            .widgets(testDashboard.widgets())
            .build();

        when(dashboardService.updateDashboard(
            eq("dash-001"),
            eq("Updated Name"),
            eq("Updated Description"),
            eq("user-456")
        )).thenReturn(CompletableFuture.completedFuture(Optional.of(updatedDashboard)));

        mockMvc.perform(put("/api/dashboards/dash-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void deleteDashboard_ShouldReturnNoContent() throws Exception {
        when(dashboardService.deleteDashboard("dash-001"))
            .thenReturn(CompletableFuture.completedFuture(true));

        mockMvc.perform(delete("/api/dashboards/dash-001"))
            .andExpect(status().isNoContent());
    }

    @Test
    void searchDashboards_ShouldReturnMatchingDashboards() throws Exception {
        when(dashboardService.searchDashboards("tenant-001", "test"))
            .thenReturn(CompletableFuture.completedFuture(List.of(testDashboard)));

        mockMvc.perform(get("/api/dashboards/search")
                .param("tenantId", "tenant-001")
                .param("keyword", "test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].dashboardId").value("dash-001"));
    }

    @Test
    void getActiveDashboards_ShouldReturnActiveDashboards() throws Exception {
        when(dashboardService.getActiveDashboards("tenant-001"))
            .thenReturn(CompletableFuture.completedFuture(List.of(testDashboard)));

        mockMvc.perform(get("/api/dashboards/tenant/tenant-001/active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void checkPermission_ShouldReturnPermissionStatus() throws Exception {
        when(dashboardService.hasPermission("dash-001", "read", "admin"))
            .thenReturn(CompletableFuture.completedFuture(true));

        mockMvc.perform(get("/api/dashboards/dash-001/permissions/check")
                .param("action", "read")
                .param("role", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasPermission").value(true));
    }
}
