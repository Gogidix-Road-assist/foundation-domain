package com.gogidix.rapidassist.service.health.monitor.service.adapters.in.web;

import com.gogidix.rapidassist.service.health.monitor.service.domain.model.ServiceHealthReport;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.in.QueryLatestServiceHealthQuery;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.in.ReportServiceHealthCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceHealthController.class)
class ServiceHealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportServiceHealthCommand reportCommand;

    @MockBean
    private QueryLatestServiceHealthQuery query;

    private RequestContext requestContext;

    @BeforeEach
    void setUp() {
        requestContext = new RequestContext("tenant-123", "US");
        RequestContextHolder.set(requestContext);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    void report_Success() throws Exception {
        mockMvc.perform(post("/api/v1/service-health/report")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceName\":\"test-service\",\"instanceId\":\"instance-1\",\"status\":\"HEALTHY\",\"details\":{}}"))
                .andExpect(status().isAccepted());

        verify(reportCommand).report(any(ServiceHealthReport.class));
    }

    @Test
    void report_NoTenantId_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(post("/api/v1/service-health/report")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceName\":\"test-service\",\"instanceId\":\"instance-1\",\"status\":\"HEALTHY\",\"details\":{}}"))
                .andExpect(status().isUnauthorized());

        verify(reportCommand, never()).report(any(ServiceHealthReport.class));
    }

    @Test
    void latest_Success() throws Exception {
        ServiceHealthReport report = new ServiceHealthReport(
                "tenant-123", "US", "test-service", "instance-1", "HEALTHY", Instant.now(), null);
        when(query.latest("tenant-123", null, 50)).thenReturn(List.of(report));

        mockMvc.perform(get("/api/v1/service-health/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceName").value("test-service"))
                .andExpect(jsonPath("$[0].instanceId").value("instance-1"))
                .andExpect(jsonPath("$[0].status").value("HEALTHY"));

        verify(query).latest("tenant-123", null, 50);
    }

    @Test
    void latest_WithServiceName_Success() throws Exception {
        ServiceHealthReport report = new ServiceHealthReport(
                "tenant-123", "US", "test-service", "instance-1", "HEALTHY", Instant.now(), null);
        when(query.latest("tenant-123", "test-service", 10)).thenReturn(List.of(report));

        mockMvc.perform(get("/api/v1/service-health/latest")
                        .param("serviceName", "test-service")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceName").value("test-service"));

        verify(query).latest("tenant-123", "test-service", 10);
    }

    @Test
    void latest_NoTenantId_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(get("/api/v1/service-health/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(query, never()).latest(anyString(), any(), anyInt());
    }
}
