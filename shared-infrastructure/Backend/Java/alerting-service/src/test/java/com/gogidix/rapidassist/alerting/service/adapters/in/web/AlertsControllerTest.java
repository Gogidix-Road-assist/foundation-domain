package com.gogidix.rapidassist.alerting.service.adapters.in.web;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertEvent;
import com.gogidix.rapidassist.alerting.service.domain.port.in.IngestAlertEventCommand;
import com.gogidix.rapidassist.alerting.service.domain.port.in.QueryAlertEventsQuery;
import com.gogidix.rapidassist.alerting.service.infrastructure.web.IngestAlertEventRequest;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertsController.class)
class AlertsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngestAlertEventCommand ingestAlertEventCommand;

    @MockBean
    private QueryAlertEventsQuery queryAlertEventsQuery;

    @BeforeEach
    void setUp() {
        RequestContext ctx = new RequestContext("test-correlation", "US", "test-tenant", "test-user", null);
        RequestContextHolder.set(ctx);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    void ingestAlert_ValidRequest_ReturnsAccepted() throws Exception {
        String requestBody = """
                {
                    "ruleId": "test-rule",
                    "severity": "HIGH",
                    "message": "Test alert",
                    "attributes": {"key": "value"}
                }
                """;

        mockMvc.perform(post("/api/v1/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());

        verify(ingestAlertEventCommand, times(1)).ingest(any(AlertEvent.class));
    }

    @Test
    void ingestAlert_MissingTenantId_ReturnsUnauthorized() throws Exception {
        RequestContextHolder.clear();

        String requestBody = """
                {
                    "ruleId": "test-rule",
                    "severity": "HIGH",
                    "message": "Test alert"
                }
                """;

        mockMvc.perform(post("/api/v1/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());

        verify(ingestAlertEventCommand, never()).ingest(any());
    }

    @Test
    void queryAlerts_ValidRequest_ReturnsAlerts() throws Exception {
        AlertEvent event = new AlertEvent(
                "test-tenant", "US", "corr-1", Instant.now(),
                "rule-1", "HIGH", "Test", null
        );
        when(queryAlertEventsQuery.query(any(), any(), any(), any(), anyInt()))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/v1/alerts")
                        .param("ruleId", "rule-1")
                        .param("limit", "10"))
                .andExpect(status().isOk());

        verify(queryAlertEventsQuery, times(1)).query(any(), any(), any(), any(), anyInt());
    }

    @Test
    void queryAlerts_MissingTenantId_ReturnsUnauthorized() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(get("/api/v1/alerts")
                        .param("limit", "10"))
                .andExpect(status().isUnauthorized());

        verify(queryAlertEventsQuery, never()).query(any(), any(), any(), any(), anyInt());
    }
}
