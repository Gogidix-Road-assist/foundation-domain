package com.gogidix.rapidassist.event.audit.service.adapters.in.web;

import com.gogidix.rapidassist.event.audit.service.domain.model.AuditEvent;
import com.gogidix.rapidassist.event.audit.service.domain.port.in.AppendAuditEventCommand;
import com.gogidix.rapidassist.event.audit.service.domain.port.in.QueryAuditEventsQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuditEventController.class)
class AuditEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppendAuditEventCommand appendCommand;

    @MockBean
    private QueryAuditEventsQuery query;

    @BeforeEach
    void setUp() {
        // RequestContext signature: correlationId, country, tenantId, userId, requestId
        RequestContext context = new RequestContext("corr-456", "US", "tenant-123", "user-789", null);
        RequestContextHolder.set(context);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    @WithMockUser
    void appendAuditEvent_Success() throws Exception {
        when(appendCommand.append(any(AuditEvent.class))).thenReturn("event-id-123");

        String requestBody = """
                {
                    "occurredAt": "2024-01-01T00:00:00Z",
                    "eventType": "USER_LOGIN",
                    "entityType": "User",
                    "entityId": "user-123",
                    "payload": {"action": "login"}
                }
                """;

        mockMvc.perform(post("/api/v1/audit/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$").value("event-id-123"));
    }

    @Test
    void appendAuditEvent_Unauthorized_WhenNoTenantId() throws Exception {
        RequestContextHolder.clear();

        String requestBody = """
                {
                    "occurredAt": "2024-01-01T00:00:00Z",
                    "eventType": "USER_LOGIN",
                    "entityType": "User",
                    "entityId": "user-123",
                    "payload": {}
                }
                """;

        mockMvc.perform(post("/api/v1/audit/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void queryAuditEvents_Success() throws Exception {
        // AuditEvent signature: tenantId, country, correlationId, occurredAt, eventType, entityType, entityId, payload
        AuditEvent event = new AuditEvent(
                "tenant-123", "US", "corr-456",
                Instant.now(), "USER_LOGIN", "User", "user-123", Map.of("action", "login")
        );

        when(query.query(eq("tenant-123"), eq("US"), eq("User"), eq("user-123"), any(), any(), eq(100)))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/v1/audit/events")
                        .param("country", "US")
                        .param("entityType", "User")
                        .param("entityId", "user-123")
                        .param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value("tenant-123"))
                .andExpect(jsonPath("$[0].eventType").value("USER_LOGIN"));
    }

    @Test
    void queryAuditEvents_Unauthorized_WhenNoTenantId() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(get("/api/v1/audit/events"))
                .andExpect(status().isUnauthorized());
    }
}
