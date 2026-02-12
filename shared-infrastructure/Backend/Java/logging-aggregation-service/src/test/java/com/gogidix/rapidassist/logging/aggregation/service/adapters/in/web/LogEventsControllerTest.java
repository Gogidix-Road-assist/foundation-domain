package com.gogidix.rapidassist.logging.aggregation.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.logging.aggregation.service.domain.model.LogEvent;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.in.IngestLogEventCommand;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.in.QueryLogEventsQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogEventsController.class)
class LogEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IngestLogEventCommand ingestCommand;

    @MockBean
    private QueryLogEventsQuery queryEventsQuery;

    @BeforeEach
    void setUp() {
        RequestContext ctx = new RequestContext("corr-456", "US", "tenant-123", "user-789", null);
        RequestContextHolder.set(ctx);
    }

    @Test
    void ingestLogEvent_Success() throws Exception {
        doNothing().when(ingestCommand).ingest(any(LogEvent.class));

        IngestLogEventRequest request = new IngestLogEventRequest(
                "corr-456", Instant.now(), "INFO", "com.example.Service", "Test message", null
        );

        mockMvc.perform(post("/api/v1/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void ingestLogEvent_MissingCorrelationId() throws Exception {
        doNothing().when(ingestCommand).ingest(any(LogEvent.class));

        IngestLogEventRequest request = new IngestLogEventRequest(
                null, Instant.now(), "INFO", "com.example.Service", "Test message", null
        );

        mockMvc.perform(post("/api/v1/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void queryLogEvents_Success() throws Exception {
        LogEvent event = new LogEvent(
                "tenant-123", "US", "corr-456", Instant.now(),
                "INFO", "com.example.Service", "Test message", null
        );
        when(queryEventsQuery.query(eq("tenant-123"), eq("corr-456"), any(), any(), eq("INFO"), eq(100)))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/v1/logs")
                        .param("correlationId", "corr-456")
                        .param("level", "INFO")
                        .param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value("tenant-123"))
                .andExpect(jsonPath("$[0].level").value("INFO"));
    }

    @Test
    void queryLogEvents_EmptyResult() throws Exception {
        when(queryEventsQuery.query(eq("tenant-123"), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
