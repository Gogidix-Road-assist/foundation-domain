package com.gogidix.rapidassist.metrics.telemetry.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.model.TelemetryEvent;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.in.IngestTelemetryEventCommand;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.in.QueryTelemetryEventsQuery;
import com.gogidix.rapidassist.metrics.telemetry.service.infrastructure.web.IngestTelemetryEventRequest;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TelemetryEventsController.class)
class TelemetryEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IngestTelemetryEventCommand ingestCommand;

    @MockBean
    private QueryTelemetryEventsQuery queryEventsQuery;

    @BeforeEach
    void setUp() {
        RequestContext ctx = new RequestContext("corr-456", "US", "tenant-123", "user-789", null);
        RequestContextHolder.set(ctx);
    }

    @Test
    void ingestTelemetryEvent_Success() throws Exception {
        doNothing().when(ingestCommand).ingest(any(TelemetryEvent.class));

        IngestTelemetryEventRequest request = new IngestTelemetryEventRequest(
                "corr-456", Instant.now(), "counter", "api_calls", 100.0, null
        );

        mockMvc.perform(post("/api/v1/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void ingestTelemetryEvent_MissingCorrelationId() throws Exception {
        doNothing().when(ingestCommand).ingest(any(TelemetryEvent.class));

        IngestTelemetryEventRequest request = new IngestTelemetryEventRequest(
                null, Instant.now(), "counter", "api_calls", 100.0, null
        );

        mockMvc.perform(post("/api/v1/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void queryTelemetryEvents_Success() throws Exception {
        TelemetryEvent event = new TelemetryEvent(
                "tenant-123", "US", "corr-456", Instant.now(),
                "counter", "api_calls", 100.0, null
        );
        when(queryEventsQuery.query(eq("tenant-123"), eq("corr-456"), any(), any(), eq("counter"), eq("api_calls"), eq(100)))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/v1/telemetry")
                        .param("correlationId", "corr-456")
                        .param("type", "counter")
                        .param("name", "api_calls")
                        .param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value("tenant-123"))
                .andExpect(jsonPath("$[0].type").value("counter"));
    }

    @Test
    void queryTelemetryEvents_EmptyResult() throws Exception {
        when(queryEventsQuery.query(eq("tenant-123"), any(), any(), any(), any(), any(), anyInt()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/telemetry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
