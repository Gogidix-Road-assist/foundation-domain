package com.gogidix.rapidassist.orchestration.alerting_service.integration;

import com.gogidix.rapidassist.orchestration.alerting_service.application.command.CreateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.application.service.AlertApplicationService;
import com.gogidix.rapidassist.orchestration.alerting_service.application.query.GetAlertQuery;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.output.AlertRepositoryPort;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify tenant isolation
 * Ensures that alerts from one tenant cannot be accessed by another tenant
 */
@SpringBootTest
@ActiveProfiles("test")
class TenantIsolationTest {

    @Autowired
    private AlertApplicationService alertService;

    @Autowired
    private AlertRepositoryPort alertRepository;

    private static final String TENANT_1 = "tenant-001";
    private static final String TENANT_2 = "tenant-002";

    @BeforeEach
    void setUp() {
        // Clean up before each test
        alertRepository.findByTenantId(TENANT_1).forEach(
            alert -> alertRepository.deleteByAlertId(alert.getAlertId())
        );
        alertRepository.findByTenantId(TENANT_2).forEach(
            alert -> alertRepository.deleteByAlertId(alert.getAlertId())
        );
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Should ensure alerts created by tenant1 are not visible to tenant2")
    void testTenantIsolationOnCreate() {
        // Given - Create alert for tenant1
        RequestContext context1 = RequestContext.builder()
            .tenantId(TENANT_1)
            .userId("user-tenant1")
            .build();
        RequestContextHolder.set(context1);

        CreateAlertCommand command = CreateAlertCommand.builder()
            .requestId("REQ-T1-001")
            .tenantId(TENANT_1)
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Tenant 1 Emergency")
            .source("API")
            .build();

        AlertDTO alert1 = alertService.createAlert(command);
        assertNotNull(alert1);
        assertEquals(TENANT_1, alert1.getTenantId());

        // When - Try to get the alert as tenant2
        RequestContext context2 = RequestContext.builder()
            .tenantId(TENANT_2)
            .userId("user-tenant2")
            .build();
        RequestContextHolder.set(context2);

        // Then - Should not find the alert
        GetAlertQuery query = GetAlertQuery.builder()
            .alertId(alert1.getAlertId())
            .tenantId(TENANT_2)
            .build();

        assertThrows(Exception.class, () -> alertService.getAlert(query));
    }

    @Test
    @DisplayName("Should ensure alerts are filtered by tenant when listing")
    void testTenantIsolationOnList() {
        // Given - Create alerts for both tenants
        RequestContext context1 = RequestContext.builder()
            .tenantId(TENANT_1)
            .userId("user-tenant1")
            .build();
        RequestContextHolder.set(context1);

        CreateAlertCommand command1 = CreateAlertCommand.builder()
            .requestId("REQ-T1-001")
            .tenantId(TENANT_1)
            .type(Alert.AlertType.BREAKDOWN)
            .severity(Alert.AlertSeverity.HIGH)
            .title("Tenant 1 Alert")
            .source("API")
            .build();

        alertService.createAlert(command1);

        RequestContext context2 = RequestContext.builder()
            .tenantId(TENANT_2)
            .userId("user-tenant2")
            .build();
        RequestContextHolder.set(context2);

        CreateAlertCommand command2 = CreateAlertCommand.builder()
            .requestId("REQ-T2-001")
            .tenantId(TENANT_2)
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Tenant 2 Alert")
            .source("API")
            .build();

        alertService.createAlert(command2);

        // When - List alerts for tenant1
        RequestContextHolder.set(context1);
        List<AlertDTO> tenant1Alerts = alertService.getActiveAlertsByTenant(TENANT_1);

        // Then - Should only see tenant1's alerts
        assertNotNull(tenant1Alerts);
        assertTrue(tenant1Alerts.stream().allMatch(a -> a.getTenantId().equals(TENANT_1)));

        // When - List alerts for tenant2
        RequestContextHolder.set(context2);
        List<AlertDTO> tenant2Alerts = alertService.getActiveAlertsByTenant(TENANT_2);

        // Then - Should only see tenant2's alerts
        assertNotNull(tenant2Alerts);
        assertTrue(tenant2Alerts.stream().allMatch(a -> a.getTenantId().equals(TENANT_2)));
    }

    @Test
    @DisplayName("Should ensure tenant cannot modify other tenant's alerts")
    void testTenantIsolationOnUpdate() {
        // Given - Create alert for tenant1
        RequestContext context1 = RequestContext.builder()
            .tenantId(TENANT_1)
            .userId("user-tenant1")
            .build();
        RequestContextHolder.set(context1);

        CreateAlertCommand command = CreateAlertCommand.builder()
            .requestId("REQ-T1-001")
            .tenantId(TENANT_1)
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Tenant 1 Emergency")
            .source("API")
            .build();

        AlertDTO alert1 = alertService.createAlert(command);

        // When - Try to acknowledge as tenant2
        RequestContext context2 = RequestContext.builder()
            .tenantId(TENANT_2)
            .userId("user-tenant2")
            .build();
        RequestContextHolder.set(context2);

        // Then - Should fail to acknowledge
        assertThrows(Exception.class, () ->
            alertService.acknowledgeAlert(
                alert1.getAlertId(),
                com.gogidix.rapidassist.orchestration.alerting_service.application.command.AcknowledgeAlertCommand.builder()
                    .acknowledgedBy("user-tenant2")
                    .assignedTo("provider-tenant2")
                    .build()
            )
        );
    }

    @Test
    @DisplayName("Should ensure tenant cannot delete other tenant's alerts")
    void testTenantIsolationOnDelete() {
        // Given - Create alert for tenant1
        RequestContext context1 = RequestContext.builder()
            .tenantId(TENANT_1)
            .userId("user-tenant1")
            .build();
        RequestContextHolder.set(context1);

        CreateAlertCommand command = CreateAlertCommand.builder()
            .requestId("REQ-T1-001")
            .tenantId(TENANT_1)
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Tenant 1 Emergency")
            .source("API")
            .build();

        AlertDTO alert1 = alertService.createAlert(command);

        // When - Try to delete as tenant2
        RequestContext context2 = RequestContext.builder()
            .tenantId(TENANT_2)
            .userId("user-tenant2")
            .build();
        RequestContextHolder.set(context2);

        // Then - Should fail to delete
        assertThrows(Exception.class, () -> alertService.deleteAlert(alert1.getAlertId()));

        // Verify alert still exists when accessed by tenant1
        RequestContextHolder.set(context1);
        GetAlertQuery query = GetAlertQuery.builder()
            .alertId(alert1.getAlertId())
            .tenantId(TENANT_1)
            .build();

        AlertDTO retrieved = alertService.getAlert(query);
        assertNotNull(retrieved);
        assertEquals(alert1.getAlertId(), retrieved.getAlertId());
    }

    @Test
    @DisplayName("Should ensure alerts by request ID are tenant-isolated")
    void testTenantIsolationOnGetByRequestId() {
        // Given - Create alerts for both tenants with same request ID
        String requestId = "REQ-SHARED-001";

        RequestContext context1 = RequestContext.builder()
            .tenantId(TENANT_1)
            .userId("user-tenant1")
            .build();
        RequestContextHolder.set(context1);

        CreateAlertCommand command1 = CreateAlertCommand.builder()
            .requestId(requestId)
            .tenantId(TENANT_1)
            .type(Alert.AlertType.BREAKDOWN)
            .severity(Alert.AlertSeverity.HIGH)
            .title("Tenant 1 Alert")
            .source("API")
            .build();

        alertService.createAlert(command1);

        RequestContext context2 = RequestContext.builder()
            .tenantId(TENANT_2)
            .userId("user-tenant2")
            .build();
        RequestContextHolder.set(context2);

        CreateAlertCommand command2 = CreateAlertCommand.builder()
            .requestId(requestId)
            .tenantId(TENANT_2)
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Tenant 2 Alert")
            .source("API")
            .build();

        alertService.createAlert(command2);

        // When - Get alerts by request ID for tenant1
        RequestContextHolder.set(context1);
        List<AlertDTO> tenant1Alerts = alertService.getAlertsByRequestId(requestId);

        // Then - Should only get tenant1's alerts
        assertEquals(1, tenant1Alerts.size());
        assertEquals(TENANT_1, tenant1Alerts.get(0).getTenantId());

        // When - Get alerts by request ID for tenant2
        RequestContextHolder.set(context2);
        List<AlertDTO> tenant2Alerts = alertService.getAlertsByRequestId(requestId);

        // Then - Should only get tenant2's alerts
        assertEquals(1, tenant2Alerts.size());
        assertEquals(TENANT_2, tenant2Alerts.get(0).getTenantId());
    }
}
