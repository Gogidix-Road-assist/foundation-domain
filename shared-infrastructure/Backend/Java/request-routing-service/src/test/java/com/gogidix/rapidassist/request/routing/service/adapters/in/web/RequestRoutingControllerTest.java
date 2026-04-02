package com.gogidix.rapidassist.request.routing.service.adapters.in.web;

import com.gogidix.rapidassist.request.routing.service.domain.port.in.DeleteRoutingRuleCommand;
import com.gogidix.rapidassist.request.routing.service.domain.port.in.ResolveRouteQuery;
import com.gogidix.rapidassist.request.routing.service.domain.port.in.UpsertRoutingRuleCommand;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestRoutingController.class)
class RequestRoutingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UpsertRoutingRuleCommand upsertCommand;

    @MockBean
    private DeleteRoutingRuleCommand deleteCommand;

    @MockBean
    private ResolveRouteQuery resolveQuery;

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
    void upsert_Success() throws Exception {
        mockMvc.perform(post("/api/v1/routing/rules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routeKey\":\"api-gateway\",\"destinationBaseUrl\":\"https://api.example.com\"}"))
                .andExpect(status().isAccepted());

        verify(upsertCommand).upsert("tenant-123", "api-gateway", "https://api.example.com");
    }

    @Test
    void upsert_NoTenantId_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(post("/api/v1/routing/rules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routeKey\":\"api-gateway\",\"destinationBaseUrl\":\"https://api.example.com\"}"))
                .andExpect(status().isUnauthorized());

        verify(upsertCommand, never()).upsert(anyString(), anyString(), anyString());
    }

    @Test
    void delete_Success() throws Exception {
        doNothing().when(deleteCommand).delete("tenant-123", "api-gateway");

        mockMvc.perform(delete("/api/v1/routing/rules/api-gateway")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(deleteCommand).delete("tenant-123", "api-gateway");
    }

    @Test
    void delete_NoTenantId_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(delete("/api/v1/routing/rules/api-gateway")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(deleteCommand, never()).delete(anyString(), anyString());
    }

    @Test
    void resolve_Success() throws Exception {
        when(resolveQuery.resolveDestinationBaseUrl("tenant-123", "api-gateway"))
                .thenReturn(Optional.of("https://api.example.com"));

        mockMvc.perform(get("/api/v1/routing/resolve/api-gateway")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeKey").value("api-gateway"))
                .andExpect(jsonPath("$.destinationBaseUrl").value("https://api.example.com"));

        verify(resolveQuery).resolveDestinationBaseUrl("tenant-123", "api-gateway");
    }

    @Test
    void resolve_NotFound_Returns404() throws Exception {
        when(resolveQuery.resolveDestinationBaseUrl("tenant-123", "api-gateway"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/routing/resolve/api-gateway")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(resolveQuery).resolveDestinationBaseUrl("tenant-123", "api-gateway");
    }

    @Test
    void resolve_NoTenantId_Returns401() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(get("/api/v1/routing/resolve/api-gateway")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(resolveQuery, never()).resolveDestinationBaseUrl(anyString(), anyString());
    }
}
