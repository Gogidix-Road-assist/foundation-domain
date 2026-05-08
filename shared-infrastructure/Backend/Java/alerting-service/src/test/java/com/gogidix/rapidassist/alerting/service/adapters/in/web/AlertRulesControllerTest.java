package com.gogidix.rapidassist.alerting.service.adapters.in.web;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertRule;
import com.gogidix.rapidassist.alerting.service.domain.port.in.ListAlertRulesQuery;
import com.gogidix.rapidassist.alerting.service.domain.port.in.UpsertAlertRuleCommand;
import com.gogidix.rapidassist.alerting.service.infrastructure.web.UpsertAlertRuleRequest;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AlertRulesController.class)
class AlertRulesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UpsertAlertRuleCommand upsertAlertRuleCommand;

    @MockBean
    private ListAlertRulesQuery listAlertRulesQuery;

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
    void upsertAlertRule_ValidRequest_ReturnsAccepted() throws Exception {
        String requestBody = """
                {
                    "ruleId": "test-rule",
                    "name": "Test Rule",
                    "severity": "HIGH",
                    "enabled": true,
                    "conditions": {"condition1": "value1"}
                }
                """;

        mockMvc.perform(post("/api/v1/alert-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());

        verify(upsertAlertRuleCommand, times(1)).upsert(any(AlertRule.class));
    }

    @Test
    void upsertAlertRule_MissingTenantId_ReturnsUnauthorized() throws Exception {
        RequestContextHolder.clear();

        String requestBody = """
                {
                    "ruleId": "test-rule",
                    "name": "Test Rule",
                    "severity": "HIGH",
                    "enabled": true
                }
                """;

        mockMvc.perform(post("/api/v1/alert-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());

        verify(upsertAlertRuleCommand, never()).upsert(any());
    }

    @Test
    void listAlertRules_ValidRequest_ReturnsRules() throws Exception {
        AlertRule rule = new AlertRule(
                "test-tenant", "rule-1", "Test Rule", "HIGH",
                true, Map.of("condition1", "value1"), Instant.now()
        );
        when(listAlertRulesQuery.list(anyString(), anyInt()))
                .thenReturn(List.of(rule));

        mockMvc.perform(get("/api/v1/alert-rules")
                        .param("limit", "10"))
                .andExpect(status().isOk());

        verify(listAlertRulesQuery, times(1)).list(anyString(), anyInt());
    }

    @Test
    void listAlertRules_MissingTenantId_ReturnsUnauthorized() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(get("/api/v1/alert-rules")
                        .param("limit", "10"))
                .andExpect(status().isUnauthorized());

        verify(listAlertRulesQuery, never()).list(anyString(), anyInt());
    }
}
