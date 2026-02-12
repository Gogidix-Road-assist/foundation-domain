package com.gogidix.rapidassist.tenant.org.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import com.gogidix.rapidassist.tenant.org.service.application.service.ComprehensiveTenantOrgService;
import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantOrgController.class)
class TenantOrgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ComprehensiveTenantOrgService tenantOrgService;

    @MockBean
    private RequestContextHolder requestContextHolder;

    private TenantOrganization sampleOrganization;

    @BeforeEach
    void setUp() {
        sampleOrganization = new TenantOrganization(
                "tenant-123",
                "org-123",
                "Test Organization",
                TenantOrganization.OrganizationType.ENTERPRISE,
                new TenantOrganization.SubscriptionPlan(
                        TenantOrganization.SubscriptionPlan.PlanType.PREMIUM,
                        Instant.now().plusSeconds(86400),
                        true
                ),
                new TenantOrganization.OrganizationSettings(
                        true,
                        true,
                        true,
                        Map.of()
                ),
                Map.of("department", "engineering"),
                new TenantOrganization.BillingInfo(
                        "billing@example.com",
                        "street",
                        "city",
                        "state",
                        "12345",
                        "US",
                        "1234567890"
                ),
                List.of(new TenantOrganization.ContactInfo(
                        "John Doe",
                        "john@example.com",
                        "1234567890",
                        TenantOrganization.ContactInfo.ContactType.BILLING
                )),
                null,
                Set.of("engineering", "production"),
                Map.of("teamSize", "50"),
                new TenantOrganization.ComplianceInfo(
                        true,
                        true,
                        Instant.now(),
                        "SOC2"
                ),
                Instant.now(),
                Instant.now(),
                null,
                TenantOrganization.OrganizationStatus.ACTIVE,
                "admin@example.com"
        );

        RequestContext context = new RequestContext("tenant-123", "correlation-123", "user-123");
        when(requestContextHolder.get()).thenReturn(Optional.of(context));
    }

    @Test
    void createOrganization_ShouldReturnCreated() throws Exception {
        when(tenantOrgService.createOrganization(any()))
                .thenReturn(CompletableFuture.completedFuture(sampleOrganization));

        mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "orgId", "org-123",
                                "orgName", "Test Organization",
                                "orgType", "ENTERPRISE",
                                "createdBy", "admin@example.com"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orgId").value("org-123"))
                .andExpect(jsonPath("$.orgName").value("Test Organization"));
    }

    @Test
    void getOrganization_ShouldReturnOrganization() throws Exception {
        when(tenantOrgService.getOrganization(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sampleOrganization)));

        mockMvc.perform(get("/api/v1/organizations/org-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgId").value("org-123"))
                .andExpect(jsonPath("$.orgName").value("Test Organization"));
    }

    @Test
    void getOrganization_WhenNotFound_ShouldReturn404() throws Exception {
        when(tenantOrgService.getOrganization(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        mockMvc.perform(get("/api/v1/organizations/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listAllOrganizations_ShouldReturnList() throws Exception {
        when(tenantOrgService.listAllOrganizations(any()))
                .thenReturn(CompletableFuture.completedFuture(List.of(sampleOrganization)));

        mockMvc.perform(get("/api/v1/organizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orgId").value("org-123"));
    }

    @Test
    void updateOrganization_ShouldReturnUpdated() throws Exception {
        when(tenantOrgService.updateOrganization(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sampleOrganization)));

        mockMvc.perform(put("/api/v1/organizations/org-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "orgName", "Updated Organization",
                                "updatedBy", "admin@example.com"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgId").value("org-123"));
    }

    @Test
    void deleteOrganization_ShouldReturnNoContent() throws Exception {
        when(tenantOrgService.deleteOrganization(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of("org-123")));

        mockMvc.perform(delete("/api/v1/organizations/org-123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listByStatus_ShouldReturnFiltered() throws Exception {
        when(tenantOrgService.listByStatus(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(List.of(sampleOrganization)));

        mockMvc.perform(get("/api/v1/organizations/by-status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}
