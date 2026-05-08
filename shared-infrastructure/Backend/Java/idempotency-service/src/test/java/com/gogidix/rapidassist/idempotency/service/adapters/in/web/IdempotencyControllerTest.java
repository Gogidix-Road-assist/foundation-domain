package com.gogidix.rapidassist.idempotency.service.adapters.in.web;

import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyRecord;
import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyStatus;
import com.gogidix.rapidassist.idempotency.service.domain.port.in.GetIdempotencyRecordQuery;
import com.gogidix.rapidassist.idempotency.service.domain.port.in.ReserveIdempotencyKeyCommand;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(IdempotencyController.class)
class IdempotencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReserveIdempotencyKeyCommand reserveIdempotencyKeyCommand;

    @MockBean
    private GetIdempotencyRecordQuery getIdempotencyRecordQuery;

    @BeforeEach
    void setUp() {
        RequestContext context = new RequestContext("corr-456", "US", "tenant-123", "user-789", null);
        RequestContextHolder.set(context);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    @WithMockUser
    void reserveIdempotencyKey_Success() throws Exception {
        IdempotencyRecord record = new IdempotencyRecord(
                "tenant-123", "key-abc123", IdempotencyStatus.RESERVED, Instant.now(), null
        );

        when(reserveIdempotencyKeyCommand.reserve(eq("tenant-123"), eq("key-abc123")))
                .thenReturn(record);

        String requestBody = """
                {
                    "key": "key-abc123"
                }
                """;

        mockMvc.perform(post("/api/v1/idempotency/reserve")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("key-abc123"))
                .andExpect(jsonPath("$.status").value("RESERVED"));
    }

    @Test
    void reserveIdempotencyKey_Unauthorized_WhenNoTenantId() throws Exception {
        RequestContextHolder.clear();

        String requestBody = """
                {
                    "key": "key-abc123"
                }
                """;

        mockMvc.perform(post("/api/v1/idempotency/reserve")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getIdempotencyRecord_Success() throws Exception {
        IdempotencyRecord record = new IdempotencyRecord(
                "tenant-123", "key-abc123", IdempotencyStatus.COMPLETED, Instant.now(), Instant.now()
        );

        when(getIdempotencyRecordQuery.get(eq("tenant-123"), eq("key-abc123")))
                .thenReturn(record);

        mockMvc.perform(get("/api/v1/idempotency/key-abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("key-abc123"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser
    void getIdempotencyRecord_NotFound() throws Exception {
        when(getIdempotencyRecordQuery.get(eq("tenant-123"), eq("key-nonexistent")))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/idempotency/key-nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getIdempotencyRecord_Unauthorized_WhenNoTenantId() throws Exception {
        RequestContextHolder.clear();

        mockMvc.perform(get("/api/v1/idempotency/key-abc123"))
                .andExpect(status().isUnauthorized());
    }
}
