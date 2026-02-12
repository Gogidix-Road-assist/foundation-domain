package com.gogidix.rapidassist.payment.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;
import com.gogidix.rapidassist.payment.service.domain.model.PaymentStatus;
import com.gogidix.rapidassist.payment.service.domain.port.in.CreatePaymentIntentCommand;
import com.gogidix.rapidassist.payment.service.domain.port.in.GetPaymentIntentQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PaymentController.
 * <p>
 * Tests the REST endpoints for payment intent creation and retrieval.
 */
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreatePaymentIntentCommand createPaymentIntentCommand;

    @MockitoBean
    private GetPaymentIntentQuery getPaymentIntentQuery;

    private final String TENANT_ID = "tenant-123";

    @BeforeEach
    void setUp() {
        RequestContext context = mock(RequestContext.class);
        when(context.tenantId()).thenReturn(TENANT_ID);
        RequestContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("POST /api/v1/payments/intents - Should create payment intent and return 201")
    void createPaymentIntent_ShouldReturn201() throws Exception {
        // Given
        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(
                new BigDecimal("10.00"),
                "USD"
        );

        PaymentIntent mockIntent = new PaymentIntent(
                "pi_test123",
                "USD",
                1000,
                PaymentStatus.REQUIRES_PAYMENT_METHOD,
                Instant.now()
        );

        when(createPaymentIntentCommand.create(eq(TENANT_ID), any(BigDecimal.class), eq("USD")))
                .thenReturn(mockIntent);

        // When & Then
        mockMvc.perform(post("/api/v1/payments/intents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.intentId").value("pi_test123"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.amount").value(1000))
                .andExpect(jsonPath("$.status").value("REQUIRES_PAYMENT_METHOD"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(createPaymentIntentCommand, times(1)).create(eq(TENANT_ID), any(BigDecimal.class), eq("USD"));
    }

    @Test
    @DisplayName("POST /api/v1/payments/intents - Should return 400 when amount is null")
    void createPaymentIntent_WithNullAmount_ShouldReturn400() throws Exception {
        // Given
        String invalidRequest = """
                {
                    "amount": null,
                    "currency": "USD"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/payments/intents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verify(createPaymentIntentCommand, never()).create(any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/v1/payments/intents - Should return 400 when amount is zero")
    void createPaymentIntent_WithZeroAmount_ShouldReturn400() throws Exception {
        // Given
        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(
                BigDecimal.ZERO,
                "USD"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/payments/intents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(createPaymentIntentCommand, never()).create(any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/v1/payments/intents - Should return 400 when currency is blank")
    void createPaymentIntent_WithBlankCurrency_ShouldReturn400() throws Exception {
        // Given
        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(
                new BigDecimal("10.00"),
                ""
        );

        // When & Then
        mockMvc.perform(post("/api/v1/payments/intents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(createPaymentIntentCommand, never()).create(any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/v1/payments/intents - Should return 401 when tenant context is missing")
    void createPaymentIntent_WithoutTenantContext_ShouldReturn401() throws Exception {
        // Given
        RequestContextHolder.clear();

        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(
                new BigDecimal("10.00"),
                "USD"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/payments/intents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(createPaymentIntentCommand, never()).create(any(), any(), any());
    }

    @Test
    @DisplayName("GET /api/v1/payments/intents/{id} - Should return payment intent")
    void getPaymentIntent_ShouldReturn200() throws Exception {
        // Given
        String intentId = "pi_test123";
        PaymentIntent mockIntent = new PaymentIntent(
                intentId,
                "USD",
                1000,
                PaymentStatus.SUCCEEDED,
                Instant.now()
        );

        when(getPaymentIntentQuery.get(eq(TENANT_ID), eq(intentId))).thenReturn(mockIntent);

        // When & Then
        mockMvc.perform(get("/api/v1/payments/intents/{intentId}", intentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intentId").value(intentId))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.amount").value(1000))
                .andExpect(jsonPath("$.status").value("SUCCEEDED"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(getPaymentIntentQuery, times(1)).get(eq(TENANT_ID), eq(intentId));
    }

    @Test
    @DisplayName("GET /api/v1/payments/intents/{id} - Should return 404 when not found")
    void getPaymentIntent_NotFound_ShouldReturn404() throws Exception {
        // Given
        String intentId = "pi_nonexistent";
        when(getPaymentIntentQuery.get(eq(TENANT_ID), eq(intentId))).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/v1/payments/intents/{intentId}", intentId))
                .andExpect(status().isNotFound());

        verify(getPaymentIntentQuery, times(1)).get(eq(TENANT_ID), eq(intentId));
    }

    @Test
    @DisplayName("GET /api/v1/payments/intents/{id} - Should return 401 when tenant context is missing")
    void getPaymentIntent_WithoutTenantContext_ShouldReturn401() throws Exception {
        // Given
        RequestContextHolder.clear();
        String intentId = "pi_test123";

        // When & Then
        mockMvc.perform(get("/api/v1/payments/intents/{intentId}", intentId))
                .andExpect(status().isUnauthorized());

        verify(getPaymentIntentQuery, never()).get(any(), any());
    }
}
