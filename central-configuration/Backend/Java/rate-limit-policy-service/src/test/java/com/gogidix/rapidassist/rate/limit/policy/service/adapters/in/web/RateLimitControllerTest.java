package com.gogidix.rapidassist.rate.limit.policy.service.adapters.in.web;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import com.gogidix.rapidassist.rate.limit.policy.service.application.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive tests for RateLimitController.
 */
@ExtendWith(MockitoExtension.class)
class RateLimitControllerTest {

    @Mock
    private RateLimitService rateLimitService;

    @InjectMocks
    private RateLimitController rateLimitController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rateLimitController).build();
    }

    private RateLimitPolicy createTestPolicy(String id, String tenantId, String policyKey) {
        return RateLimitPolicy.builder()
                .id(id)
                .tenantId(tenantId)
                .policyKey(policyKey)
                .name("Test Rate Limit")
                .description("Test rate limit policy")
                .limitType(RateLimitPolicy.LimitType.USER_BASED)
                .config(new RateLimitPolicy.RateLimitConfig(60, 1000, 10000, 10, 60000, "token-bucket"))
                .scope(new RateLimitPolicy.Scope(Set.of("/api/*"), Set.of(), Set.of(), Map.of()))
                .enabled(true)
                .environment("production")
                .createdBy("admin@example.com")
                .version(1)
                .build();
    }

    @Nested
    class HealthTests {

        @Test
        void health_ReturnsUpStatus() throws Exception {
            mockMvc.perform(get("/api/rate-limits/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.service").value("rate-limit-policy-service"));
        }
    }

    @Nested
    class GetPoliciesTests {

        @Test
        void getPolicies_ReturnsPolicies() throws Exception {
            // Arrange
            List<RateLimitPolicy> policies = List.of(
                    createTestPolicy("policy-1", "tenant-1", "LIMIT1"),
                    createTestPolicy("policy-2", "tenant-1", "LIMIT2")
            );

            when(rateLimitService.getPolicies("tenant-1"))
                    .thenReturn(CompletableFuture.completedFuture(policies));

            // Act & Assert
            mockMvc.perform(get("/api/rate-limits/policies")
                            .param("tenantId", "tenant-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].policyKey").value("LIMIT1"))
                    .andExpect(jsonPath("$[1].policyKey").value("LIMIT2"));
        }

        @Test
        void getPolicies_Empty_ReturnsEmptyList() throws Exception {
            // Arrange
            when(rateLimitService.getPolicies("tenant-1"))
                    .thenReturn(CompletableFuture.completedFuture(List.of()));

            // Act & Assert
            mockMvc.perform(get("/api/rate-limits/policies")
                            .param("tenantId", "tenant-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class GetPolicyTests {

        @Test
        void getPolicy_ReturnsPolicy() throws Exception {
            // Arrange
            RateLimitPolicy policy = createTestPolicy("policy-1", "tenant-1", "API_LIMIT");

            when(rateLimitService.getPolicy("tenant-1", "API_LIMIT"))
                    .thenReturn(CompletableFuture.completedFuture(policy));

            // Act & Assert
            mockMvc.perform(get("/api/rate-limits/policies/API_LIMIT")
                            .param("tenantId", "tenant-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.policyKey").value("API_LIMIT"));
        }
    }

    @Nested
    class CreatePolicyTests {

        @Test
        void createPolicy_Success_ReturnsCreated() throws Exception {
            // Arrange
            RateLimitPolicy policy = createTestPolicy("policy-1", "tenant-1", "NEW_LIMIT");

            when(rateLimitService.createPolicy(
                    eq("tenant-1"), eq("NEW_LIMIT"), eq("New Rate Limit"),
                    any(RateLimitPolicy.RateLimitConfig.class), eq("admin@example.com"))
            ).thenReturn(CompletableFuture.completedFuture(policy));

            String requestBody = """
                    {
                        "tenantId": "tenant-1",
                        "policyKey": "NEW_LIMIT",
                        "name": "New Rate Limit",
                        "config": {
                            "requestsPerMinute": 60,
                            "requestsPerHour": 1000,
                            "requestsPerDay": 10000,
                            "burstCapacity": 10,
                            "windowSizeMs": 60000,
                            "algorithm": "token-bucket"
                        },
                        "createdBy": "admin@example.com"
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post("/api/rate-limits/policies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.policyKey").value("NEW_LIMIT"));
        }
    }

    @Nested
    class DeletePolicyTests {

        @Test
        void deletePolicy_Success_ReturnsNoContent() throws Exception {
            // Arrange
            when(rateLimitService.deletePolicy("policy-1"))
                    .thenReturn(CompletableFuture.completedFuture(true));

            // Act & Assert
            mockMvc.perform(delete("/api/rate-limits/policies/policy-1"))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class CheckRateLimitTests {

        @Test
        void checkRateLimit_Allowed_ReturnsTrue() throws Exception {
            // Arrange
            when(rateLimitService.checkRateLimit("tenant-1", "user-123", "/api/test"))
                    .thenReturn(CompletableFuture.completedFuture(true));

            String requestBody = """
                    {
                        "tenantId": "tenant-1",
                        "identifier": "user-123",
                        "endpoint": "/api/test"
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post("/api/rate-limits/check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.allowed").value(true));
        }

        @Test
        void checkRateLimit_NotAllowed_ReturnsFalse() throws Exception {
            // Arrange
            when(rateLimitService.checkRateLimit("tenant-1", "user-123", "/api/test"))
                    .thenReturn(CompletableFuture.completedFuture(false));

            String requestBody = """
                    {
                        "tenantId": "tenant-1",
                        "identifier": "user-123",
                        "endpoint": "/api/test"
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post("/api/rate-limits/check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.allowed").value(false));
        }
    }

    @Nested
    class ResetCountersTests {

        @Test
        void resetCounters_Success_ReturnsOk() throws Exception {
            // Arrange
            when(rateLimitService.resetCounters("tenant-1", "user-123"))
                    .thenReturn(CompletableFuture.completedFuture(null));

            String requestBody = """
                    {
                        "tenantId": "tenant-1",
                        "identifier": "user-123"
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post("/api/rate-limits/reset")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk());
        }
    }
}
