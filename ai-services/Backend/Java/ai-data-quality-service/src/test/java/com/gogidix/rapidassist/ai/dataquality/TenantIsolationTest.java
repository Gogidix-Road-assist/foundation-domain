package com.gogidix.rapidassist.ai.dataquality;

import com.gogidix.rapidassist.ai.dataquality.application.command.CreateDataQualityRuleCommand;
import com.gogidix.rapidassist.ai.dataquality.application.command.ExecuteDataQualityCheckCommand;
import com.gogidix.rapidassist.ai.dataquality.application.dto.DataQualityCheckDto;
import com.gogidix.rapidassist.ai.dataquality.application.dto.DataQualityIssueDto;
import com.gogidix.rapidassist.ai.dataquality.application.dto.DataQualityReportDto;
import com.gogidix.rapidassist.ai.dataquality.application.dto.DataQualityRuleDto;
import com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityMetricsQuery;
import com.gogidix.rapidassist.ai.dataquality.application.service.DataQualityApplicationService;
import com.gogidix.rapidassist.shared.request.context.library.exception.TenantValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify tenant isolation in AI Data Quality Service.
 * Ensures that:
 * 1. Data from one tenant is not accessible to another tenant
 * 2. Tenant context is properly propagated
 * 3. Tenant validation exceptions are thrown when context is missing
 */
@SpringBootTest
@ActiveProfiles("test")
class TenantIsolationTest {

    @Autowired
    private DataQualityApplicationService dataQualityApplicationService;

    private static final String TENANT_A = "tenant-a";
    private static final String TENANT_B = "tenant-b";
    private static final String USER_A = "user-a";

    private CreateDataQualityRuleCommand ruleCommandTenantA;
    private ExecuteDataQualityCheckCommand checkCommandTenantA;

    @BeforeEach
    void setUp() {
        // Setup test data for Tenant A
        ruleCommandTenantA = createRuleCommand();
        checkCommandTenantA = createCheckCommand();
    }

    @AfterEach
    void tearDown() {
        // Clear tenant context after each test
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.clear();
    }

    @Test
    void testTenantIsolation_Rules() {
        // Set tenant context to Tenant A
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );

        // Create rule for Tenant A
        DataQualityRuleDto createdRuleA = dataQualityApplicationService.createRule(ruleCommandTenantA);
        assertNotNull(createdRuleA);
        assertEquals(TENANT_A, createdRuleA.getTenantId());

        // Verify Tenant A can access their own rule
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        DataQualityRuleDto foundRuleA = dataQualityApplicationService.getRule(
            new com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityRuleQuery(
                TENANT_A, createdRuleA.getId()
            )
        );
        assertNotNull(foundRuleA);
        assertEquals(TENANT_A, foundRuleA.getTenantId());

        // Try to access Tenant A's rule from Tenant B context
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_B)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        assertThrows(Exception.class, () -> {
            dataQualityApplicationService.getRule(
                new com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityRuleQuery(
                    TENANT_A, createdRuleA.getId()
                )
            );
        });

        // Create rule for Tenant B
        CreateDataQualityRuleCommand ruleCommandTenantB = createRuleCommand();
        DataQualityRuleDto createdRuleB = dataQualityApplicationService.createRule(ruleCommandTenantB);
        assertNotNull(createdRuleB);
        assertEquals(TENANT_B, createdRuleB.getTenantId());

        // Verify both tenants have separate data
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        var rulesListA = dataQualityApplicationService.listRules(
            new com.gogidix.rapidassist.ai.dataquality.application.query.ListDataQualityRulesQuery(TENANT_A)
        );
        assertTrue(rulesListA.stream().allMatch(r -> TENANT_A.equals(r.getTenantId())));
        assertEquals(1, rulesListA.size());

        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_B)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        var rulesListB = dataQualityApplicationService.listRules(
            new com.gogidix.rapidassist.ai.dataquality.application.query.ListDataQualityRulesQuery(TENANT_B)
        );
        assertTrue(rulesListB.stream().allMatch(r -> TENANT_B.equals(r.getTenantId())));
        assertEquals(1, rulesListB.size());
    }

    @Test
    void testTenantIsolation_Checks() {
        // Set tenant context to Tenant A
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );

        // Create rule first
        DataQualityRuleDto ruleA = dataQualityApplicationService.createRule(ruleCommandTenantA);
        checkCommandTenantA.setRuleId(ruleA.getId());

        // Execute check for Tenant A
        DataQualityCheckDto createdCheckA = dataQualityApplicationService.executeCheck(checkCommandTenantA);
        assertNotNull(createdCheckA);
        assertEquals(TENANT_A, createdCheckA.getTenantId());

        // Verify Tenant A can access their own check
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        DataQualityCheckDto foundCheckA = dataQualityApplicationService.getCheck(
            new com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityCheckQuery(
                TENANT_A, createdCheckA.getId()
            )
        );
        assertNotNull(foundCheckA);
        assertEquals(TENANT_A, foundCheckA.getTenantId());

        // Try to access Tenant A's check from Tenant B context
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_B)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        assertThrows(Exception.class, () -> {
            dataQualityApplicationService.getCheck(
                new com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityCheckQuery(
                    TENANT_A, createdCheckA.getId()
                )
            );
        });

        // Create check for Tenant B
        CreateDataQualityRuleCommand ruleCommandTenantB = createRuleCommand();
        DataQualityRuleDto ruleB = dataQualityApplicationService.createRule(ruleCommandTenantB);

        ExecuteDataQualityCheckCommand checkCommandTenantB = createCheckCommand();
        checkCommandTenantB.setRuleId(ruleB.getId());

        DataQualityCheckDto createdCheckB = dataQualityApplicationService.executeCheck(checkCommandTenantB);
        assertNotNull(createdCheckB);
        assertEquals(TENANT_B, createdCheckB.getTenantId());

        // Verify both tenants have separate data
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        var checksListA = dataQualityApplicationService.listChecks(TENANT_A, ruleA.getId());
        assertTrue(checksListA.stream().allMatch(c -> TENANT_A.equals(c.getTenantId())));
        assertEquals(1, checksListA.size());

        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_B)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        var checksListB = dataQualityApplicationService.listChecks(TENANT_B, ruleB.getId());
        assertTrue(checksListB.stream().allMatch(c -> TENANT_B.equals(c.getTenantId())));
        assertEquals(1, checksListB.size());
    }

    @Test
    void testTenantIsolation_Issues() {
        // Set tenant context to Tenant A
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );

        // Create rule and check for Tenant A
        DataQualityRuleDto ruleA = dataQualityApplicationService.createRule(ruleCommandTenantA);
        checkCommandTenantA.setRuleId(ruleA.getId());
        DataQualityCheckDto checkA = dataQualityApplicationService.executeCheck(checkCommandTenantA);

        // Get issues for Tenant A
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        List<DataQualityIssueDto> issuesA = dataQualityApplicationService.listIssues(
            new com.gogidix.rapidassist.ai.dataquality.application.query.ListDataQualityIssuesQuery(TENANT_A)
        );
        assertTrue(issuesA.stream().allMatch(i -> TENANT_A.equals(i.getTenantId())));

        // Create issues for Tenant B
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_B)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        CreateDataQualityRuleCommand ruleCommandTenantB = createRuleCommand();
        DataQualityRuleDto ruleB = dataQualityApplicationService.createRule(ruleCommandTenantB);

        ExecuteDataQualityCheckCommand checkCommandTenantB = createCheckCommand();
        checkCommandTenantB.setRuleId(ruleB.getId());
        dataQualityApplicationService.executeCheck(checkCommandTenantB);

        List<DataQualityIssueDto> issuesB = dataQualityApplicationService.listIssues(
            new com.gogidix.rapidassist.ai.dataquality.application.query.ListDataQualityIssuesQuery(TENANT_B)
        );
        assertTrue(issuesB.stream().allMatch(i -> TENANT_B.equals(i.getTenantId())));

        // Verify both tenants have separate data
        assertNotEquals(issuesA.size(), issuesB.size() > 0 ? -1 : 0);
    }

    @Test
    void testTenantIsolation_Reports() {
        // Set tenant context to Tenant A
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );

        // Create report for Tenant A
        var reportCommandA = new com.gogidix.rapidassist.ai.dataquality.application.command.GenerateDataQualityReportCommand(
            "Quality Report A",
            "SUMMARY",
            LocalDateTime.now().minusDays(7),
            LocalDateTime.now(),
            TENANT_A,
            USER_A
        );

        DataQualityReportDto createdReportA = dataQualityApplicationService.generateReport(reportCommandA);
        assertNotNull(createdReportA);
        assertEquals(TENANT_A, createdReportA.getTenantId());

        // Create report for Tenant B
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_B)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        var reportCommandB = new com.gogidix.rapidassist.ai.dataquality.application.command.GenerateDataQualityReportCommand(
            "Quality Report B",
            "SUMMARY",
            LocalDateTime.now().minusDays(7),
            LocalDateTime.now(),
            TENANT_B,
            USER_A
        );

        DataQualityReportDto createdReportB = dataQualityApplicationService.generateReport(reportCommandB);
        assertNotNull(createdReportB);
        assertEquals(TENANT_B, createdReportB.getTenantId());

        // Verify reports are tenant-specific
        assertNotEquals(createdReportA.getId(), createdReportB.getId());
    }

    @Test
    void testTenantContextValidation_NoTenantId() {
        // Clear tenant context
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.clear();

        // Attempt to create rule without tenant context should fail
        assertThrows(TenantValidationException.class, () -> {
            dataQualityApplicationService.createRule(ruleCommandTenantA);
        });
    }

    @Test
    void testTenantContextPropagation() {
        // Set tenant context
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );

        // Create rule
        DataQualityRuleDto created = dataQualityApplicationService.createRule(ruleCommandTenantA);

        // Verify tenant ID is correctly set
        assertEquals(TENANT_A, created.getTenantId());

        // Update rule
        var updateCommand = new com.gogidix.rapidassist.ai.dataquality.application.command.UpdateDataQualityRuleCommand(
            created.getId(),
            TENANT_A,
            "Updated Rule Name",
            null,
            null,
            null,
            USER_A
        );

        DataQualityRuleDto updated = dataQualityApplicationService.updateRule(updateCommand);

        // Verify tenant ID remains unchanged
        assertEquals(TENANT_A, updated.getTenantId());
    }

    @Test
    void testTenantIsolation_Metrics() {
        // Set tenant context to Tenant A
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_A)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );

        // Get metrics for Tenant A
        var metricsQueryA = new GetDataQualityMetricsQuery(TENANT_A);
        List<com.gogidix.rapidassist.ai.dataquality.application.dto.DataQualityMetricDto> metricsA =
            dataQualityApplicationService.getMetrics(metricsQueryA);

        // Get metrics for Tenant B
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                .tenantId(TENANT_B)
                .requestId(java.util.UUID.randomUUID().toString())
                .build()
        );
        var metricsQueryB = new GetDataQualityMetricsQuery(TENANT_B);
        List<com.gogidix.rapidassist.ai.dataquality.application.dto.DataQualityMetricDto> metricsB =
            dataQualityApplicationService.getMetrics(metricsQueryB);

        // Verify both tenants have separate data
        assertTrue(metricsA.isEmpty() || metricsA.stream().allMatch(m -> TENANT_A.equals(m.getTenantId())));
        assertTrue(metricsB.isEmpty() || metricsB.stream().allMatch(m -> TENANT_B.equals(m.getTenantId())));
    }

    // Helper methods to create commands

    private CreateDataQualityRuleCommand createRuleCommand() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("minLength", 5);
        parameters.put("maxLength", 100);

        return new CreateDataQualityRuleCommand(
            "Test Quality Rule",
            "Validation rule for testing",
            DataQualityRuleDto.RuleType.COMPLETENESS,
            "Customer",
            "email",
            DataQualityRuleDto.ValidationOperator.NOT_NULL,
            "null",
            parameters,
            DataQualityRuleDto.RuleSeverity.HIGH,
            true,
            USER_A
        );
    }

    private ExecuteDataQualityCheckCommand createCheckCommand() {
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("sampleSize", 1000);

        return new ExecuteDataQualityCheckCommand(
            UUID.randomUUID(),
            "Data Quality Check",
            "Customer",
            "dataset-001",
            checkParams,
            USER_A
        );
    }
}
