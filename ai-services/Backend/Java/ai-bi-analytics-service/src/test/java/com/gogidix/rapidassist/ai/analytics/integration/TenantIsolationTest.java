package com.gogidix.rapidassist.ai.analytics.integration;

import com.gogidix.rapidassist.ai.analytics.application.service.DashboardApplicationService;
import com.gogidix.rapidassist.ai.analytics.domain.model.Dashboard;
import com.gogidix.rapidassist.ai.analytics.domain.repository.DashboardRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.tenant.RequestContextHolder;
import com.gogidix.rapidassist.ai.analytics.infrastructure.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify tenant isolation
 * Ensures that data from one tenant cannot be accessed by another tenant
 */
@SpringBootTest
@ActiveProfiles("test")
class TenantIsolationTest {

    @Autowired
    private DashboardRepositoryPort dashboardRepository;

    @Autowired
    private DashboardApplicationService dashboardApplicationService;

    private static final String TENANT_1 = "tenant-1";
    private static final String TENANT_2 = "tenant-2";
    private static final String USER_ID = "test-user";

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        dashboardRepository.findByTenantId(TENANT_1).forEach(d ->
                dashboardRepository.deleteById(d.getId()));
        dashboardRepository.findByTenantId(TENANT_2).forEach(d ->
                dashboardRepository.deleteById(d.getId()));
    }

    @Test
    void testTenantIsolation_ShouldNotAccessOtherTenantData() {
        // Setup tenant 1 context
        TenantContext context1 = new TenantContext(TENANT_1, USER_ID, UUID.randomUUID().toString());
        RequestContextHolder.setContext(context1);

        // Create dashboard for tenant 1
        Dashboard dashboard1 = Dashboard.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_1)
                .name("Dashboard 1")
                .description("Tenant 1 Dashboard")
                .isPublic(false)
                .isActive(true)
                .build();

        Dashboard savedDashboard1 = dashboardRepository.save(dashboard1);
        assertNotNull(savedDashboard1);
        assertEquals(TENANT_1, savedDashboard1.getTenantId());

        // Clear context
        RequestContextHolder.clearContext();

        // Setup tenant 2 context
        TenantContext context2 = new TenantContext(TENANT_2, USER_ID, UUID.randomUUID().toString());
        RequestContextHolder.setContext(context2);

        // Create dashboard for tenant 2
        Dashboard dashboard2 = Dashboard.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_2)
                .name("Dashboard 2")
                .description("Tenant 2 Dashboard")
                .isPublic(false)
                .isActive(true)
                .build();

        Dashboard savedDashboard2 = dashboardRepository.save(dashboard2);
        assertNotNull(savedDashboard2);
        assertEquals(TENANT_2, savedDashboard2.getTenantId());

        // Verify tenant 2 cannot access tenant 1's dashboard
        List<Dashboard> tenant2Dashboards = dashboardRepository.findByTenantId(TENANT_2);
        assertEquals(1, tenant2Dashboards.size());
        assertEquals(TENANT_2, tenant2Dashboards.get(0).getTenantId());

        // Verify tenant 2 cannot find tenant 1's dashboard by ID
        assertTrue(dashboardRepository.findById(savedDashboard1.getId()).isEmpty());

        // Clear context
        RequestContextHolder.clearContext();
    }

    @Test
    void testTenantFiltering_ShouldOnlyReturnOwnTenantData() {
        // Setup tenant 1 context
        TenantContext context1 = new TenantContext(TENANT_1, USER_ID, UUID.randomUUID().toString());
        RequestContextHolder.setContext(context1);

        // Create multiple dashboards for tenant 1
        for (int i = 1; i <= 3; i++) {
            Dashboard dashboard = Dashboard.builder()
                    .id(UUID.randomUUID())
                    .tenantId(TENANT_1)
                    .name("Dashboard " + i)
                    .description("Tenant 1 Dashboard " + i)
                    .isPublic(false)
                    .isActive(true)
                    .build();
            dashboardRepository.save(dashboard);
        }

        List<Dashboard> tenant1Dashboards = dashboardRepository.findByTenantId(TENANT_1);
        assertEquals(3, tenant1Dashboards.size());

        // Clear context
        RequestContextHolder.clearContext();

        // Setup tenant 2 context
        TenantContext context2 = new TenantContext(TENANT_2, USER_ID, UUID.randomUUID().toString());
        RequestContextHolder.setContext(context2);

        // Create dashboards for tenant 2
        Dashboard dashboard = Dashboard.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_2)
                .name("Tenant 2 Dashboard")
                .description("Tenant 2 Dashboard")
                .isPublic(false)
                .isActive(true)
                .build();
        dashboardRepository.save(dashboard);

        List<Dashboard> tenant2Dashboards = dashboardRepository.findByTenantId(TENANT_2);
        assertEquals(1, tenant2Dashboards.size());

        // Verify that when tenant 2 queries, they only get their data
        List<Dashboard> allTenant2Data = dashboardRepository.findByTenantId(TENANT_2);
        assertTrue(allTenant2Data.stream().allMatch(d -> TENANT_2.equals(d.getTenantId())));

        // Clear context
        RequestContextHolder.clearContext();
    }

    @Test
    void testTenantContext_ShouldPropagateAcrossLayers() {
        // Setup tenant context
        String correlationId = UUID.randomUUID().toString();
        TenantContext context = new TenantContext(TENANT_1, USER_ID, correlationId);
        RequestContextHolder.setContext(context);

        // Verify context is accessible
        assertEquals(TENANT_1, RequestContextHolder.getTenantId());
        assertEquals(USER_ID, RequestContextHolder.getUserId());
        assertEquals(correlationId, RequestContextHolder.getCorrelationId());

        // Verify hasContext
        assertTrue(RequestContextHolder.hasContext());

        // Clear and verify
        RequestContextHolder.clearContext();
        assertFalse(RequestContextHolder.hasContext());

        assertThrows(IllegalStateException.class, RequestContextHolder::getTenantId);
    }
}
