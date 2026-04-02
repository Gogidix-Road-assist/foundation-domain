package com.gogidix.rapidassist.audit.correlation.service.adapters.in.web;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.in.GetCorrelationQuery;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.in.UpsertCorrelationCommand;
import com.gogidix.rapidassist.audit.correlation.service.infrastructure.web.UpsertCorrelationRequest;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CorrelationController.class)
class CorrelationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UpsertCorrelationCommand upsertCorrelationCommand;

    @MockBean
    private GetCorrelationQuery getCorrelationQuery;

    @BeforeEach
    void setUp() {
        RequestContext ctx = new RequestContext("test-tenant", "US", "test-correlation");
        RequestContextHolder.set(ctx);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    void upsertCorrelation_ValidRequest_ReturnsAccepted() throws Exception {
        String requestBody = """
                {
                    "correlationId": "test-correlation",
                    "tags": ["tag1", "tag2"]
                }
                """;

        mockMvc.perform(post("/api/v1/correlations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());

        verify(upsertCorrelationCommand, times(1)).upsert(any(CorrelationRecord.class));
    }

    @Test
    void upsertCorrelation_MissingTenantId_ReturnsUnauthorized() throws Exception {
        RequestContextHolder.clear();

        String requestBody = """
                {
                    "correlationId": "test-correlation",
                    "tags": ["tag1"]
                }
                """;

        mockMvc.perform(post("/api/v1/correlations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());

        verify(upsertCorrelationCommand, never()).upsert(any());
    }

    @Test
    void getCorrelation_ValidRequest_ReturnsCorrelation() throws Exception {
        CorrelationRecord record = new CorrelationRecord(
                "test-tenant", "US", "corr-1", Instant.now(),
                List.of("tag1", "tag2")
        );
        when(getCorrelationQuery.get(anyString(), anyString()))
                .thenReturn(Optional.of(record));

        mockMvc.perform(get("/api/v1/correlations/corr-1"))
                .andExpect(status().isOk());

        verify(getCorrelationQuery, times(1)).get(anyString(), anyString());
    }

    @Test
    void getCorrelation_NotFound_Returns404() throws Exception {
        when(getCorrelationQuery.get(anyString(), anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/correlations/nonexistent"))
                .andExpect(status().isNotFound());

        verify(getCorrelationQuery, times(1)).get(anyString(), anyString());
    }

    @Test
    void getCorrelation_MissingTenantId_ReturnsUnauthorized() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(get("/api/v1/correlations/corr-1"))
                .andExpect(status().isUnauthorized());

        verify(getCorrelationQuery, never()).get(anyString(), anyString());
    }
}
