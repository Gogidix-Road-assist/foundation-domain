package com.gogidix.rapidassist.session.token.service.adapters.in.web;

import com.gogidix.rapidassist.session.token.service.domain.model.SessionIntrospectionResult;
import com.gogidix.rapidassist.session.token.service.domain.model.SessionToken;
import com.gogidix.rapidassist.session.token.service.domain.port.in.IntrospectSessionTokenQuery;
import com.gogidix.rapidassist.session.token.service.domain.port.in.IssueSessionTokenCommand;
import com.gogidix.rapidassist.session.token.service.domain.port.in.RevokeSessionTokenCommand;
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

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(SessionTokenController.class)
class SessionTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueSessionTokenCommand issueSessionTokenCommand;

    @MockBean
    private IntrospectSessionTokenQuery introspectSessionTokenQuery;

    @MockBean
    private RevokeSessionTokenCommand revokeSessionTokenCommand;

    private RequestContext requestContext;

    @BeforeEach
    void setUp() {
        requestContext = new RequestContext("corr-456", "US", "tenant-123", "user-789", null);
        RequestContextHolder.set(requestContext);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    void issue_Success() throws Exception {
        SessionToken token = new SessionToken("tenant-123", "user123", "test-token", Instant.now(), Instant.now().plusSeconds(3600));
        when(issueSessionTokenCommand.issue(eq("tenant-123"), eq("user123"), any(Duration.class)))
                .thenReturn(token);

        mockMvc.perform(post("/api/v1/session-tokens/issue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subject\":\"user123\",\"ttlSeconds\":3600}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"));

        verify(issueSessionTokenCommand).issue(eq("tenant-123"), eq("user123"), any(Duration.class));
    }

    @Test
    void issue_NoTenantId_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(post("/api/v1/session-tokens/issue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subject\":\"user123\",\"ttlSeconds\":3600}"))
                .andExpect(status().isUnauthorized());

        verify(issueSessionTokenCommand, never()).issue(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void introspect_Success() throws Exception {
        SessionIntrospectionResult result = new SessionIntrospectionResult(true, "tenant-123", "user123", Instant.now().plusSeconds(3600), null);
        when(introspectSessionTokenQuery.introspect("tenant-123", "test-token")).thenReturn(result);

        mockMvc.perform(post("/api/v1/session-tokens/introspect")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"test-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.subject").value("user123"));

        verify(introspectSessionTokenQuery).introspect("tenant-123", "test-token");
    }

    @Test
    void introspect_NoTenantId_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(post("/api/v1/session-tokens/introspect")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"test-token\"}"))
                .andExpect(status().isUnauthorized());

        verify(introspectSessionTokenQuery, never()).introspect(anyString(), anyString());
    }

    @Test
    void revoke_Success() throws Exception {
        doNothing().when(revokeSessionTokenCommand).revoke("tenant-123", "test-token");

        mockMvc.perform(post("/api/v1/session-tokens/revoke")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"test-token\"}"))
                .andExpect(status().isNoContent());

        verify(revokeSessionTokenCommand).revoke("tenant-123", "test-token");
    }

    @Test
    void revoke_NoTenantId_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(post("/api/v1/session-tokens/revoke")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"test-token\"}"))
                .andExpect(status().isUnauthorized());

        verify(revokeSessionTokenCommand, never()).revoke(anyString(), anyString());
    }
}
