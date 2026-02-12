package com.gogidix.rapidassist.waf.policy.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import com.gogidix.rapidassist.waf.policy.service.domain.model.WafDecision;
import com.gogidix.rapidassist.waf.policy.service.domain.port.in.EvaluateWafRequestCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WafPolicyController.class)
class WafPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EvaluateWafRequestCommand evaluateWafRequestCommand;

    @MockBean
    private RequestContextHolder requestContextHolder;

    @BeforeEach
    void setUp() {
        RequestContext context = new RequestContext("tenant-123", "correlation-123", "user-123");
        when(requestContextHolder.get()).thenReturn(Optional.of(context));
    }

    @Test
    void evaluate_WithValidRequest_ShouldAllow() throws Exception {
        WafDecision decision = new WafDecision(
                true,
                "rule-123",
                WafDecision.Action.ALLOW,
                "Request allowed",
                Map.of("score", "0.1")
        );

        when(evaluateWafRequestCommand.evaluate(any(), any()))
                .thenReturn(decision);

        mockMvc.perform(post("/api/v1/waf/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "request", Map.of(
                                        "ip", "192.168.1.1",
                                        "path", "/api/test",
                                        "method", "GET"
                                )
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.action").value("ALLOW"))
                .andExpect(jsonPath("$.reason").value("Request allowed"));
    }

    @Test
    void evaluate_WithSuspiciousRequest_ShouldBlock() throws Exception {
        WafDecision decision = new WafDecision(
                false,
                "rule-456",
                WafDecision.Action.BLOCK,
                "SQL injection detected",
                Map.of("score", "0.95", "pattern", "sql-injection")
        );

        when(evaluateWafRequestCommand.evaluate(any(), any()))
                .thenReturn(decision);

        mockMvc.perform(post("/api/v1/waf/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "request", Map.of(
                                        "ip", "192.168.1.100",
                                        "path", "/api/users",
                                        "method", "POST",
                                        "body", "SELECT * FROM users"
                                )
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(false))
                .andExpect(jsonPath("$.action").value("BLOCK"))
                .andExpect(jsonPath("$.reason").value("SQL injection detected"));
    }

    @Test
    void evaluate_WithNullRequest_ShouldStillWork() throws Exception {
        WafDecision decision = new WafDecision(
                true,
                "rule-default",
                WafDecision.Action.ALLOW,
                "Default allow",
                Map.of()
        );

        when(evaluateWafRequestCommand.evaluate(any(), any()))
                .thenReturn(decision);

        mockMvc.perform(post("/api/v1/waf/evaluate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true));
    }

    @Test
    void evaluate_WithRateLimitExceeded_ShouldThrottle() throws Exception {
        WafDecision decision = new WafDecision(
                false,
                "rule-rate-limit",
                WafDecision.Action.THROTTLE,
                "Rate limit exceeded",
                Map.of("limit", "100", "window", "60s")
        );

        when(evaluateWafRequestCommand.evaluate(any(), any()))
                .thenReturn(decision);

        mockMvc.perform(post("/api/v1/waf/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "request", Map.of(
                                        "ip", "192.168.1.50",
                                        "path", "/api/data"
                                )
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(false))
                .andExpect(jsonPath("$.action").value("THROTTLE"));
    }
}
