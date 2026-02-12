package com.gogidix.rapidassist.ai.dataquality.application.service;

import com.gogidix.rapidassist.ai.dataquality.application.command.*;
import com.gogidix.rapidassist.ai.dataquality.application.dto.*;
import com.gogidix.rapidassist.ai.dataquality.domain.model.*;
import com.gogidix.rapidassist.ai.dataquality.domain.repository.*;
import com.gogidix.rapidassist.ai.dataquality.domain.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DataQualityApplicationService
 */
@ExtendWith(MockitoExtension.class)
class DataQualityApplicationServiceTest {

    @Mock
    private DataQualityRuleRepositoryPort ruleRepository;

    @Mock
    private DataQualityCheckRepositoryPort checkRepository;

    @Mock
    private DataQualityIssueRepositoryPort issueRepository;

    @Mock
    private DataQualityReportRepositoryPort reportRepository;

    @Mock
    private DataQualityMetricRepositoryPort metricRepository;

    @InjectMocks
    private DataQualityApplicationService applicationService;

    private String tenantId;
    private UUID ruleId;

    @BeforeEach
    void setUp() {
        tenantId = "test-tenant";
        ruleId = UUID.randomUUID();
        TenantContext.setTenantId(tenantId);
    }

    @Test
    void testCreateRule() {
        // Given
        CreateDataQualityRuleCommand command = CreateDataQualityRuleCommand.builder()
                .tenantId(tenantId)
                .name("Email Validation Rule")
                .description("Validate email addresses")
                .ruleType(DataQualityRule.RuleType.VALIDITY)
                .entityType("Customer")
                .attributeName("email")
                .operator(DataQualityRule.ValidationOperator.MATCHES_REGEX)
                .thresholdValue("^[A-Za-z0-9+_.-]+@(.+)$")
                .parameters(new HashMap<>())
                .severity(DataQualityRule.RuleSeverity.HIGH)
                .active(true)
                .createdBy("admin")
                .build();

        DataQualityRule savedRule = DataQualityRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name("Email Validation Rule")
                .ruleType(DataQualityRule.RuleType.VALIDITY)
                .entityType("Customer")
                .severity(DataQualityRule.RuleSeverity.HIGH)
                .active(true)
                .build();

        when(ruleRepository.save(any(DataQualityRule.class))).thenReturn(savedRule);

        // When
        DataQualityRuleDto result = applicationService.createRule(command);

        // Then
        assertNotNull(result);
        assertEquals("Email Validation Rule", result.getName());
        assertEquals(DataQualityRule.RuleType.VALIDITY, result.getRuleType());
        assertEquals(DataQualityRule.RuleSeverity.HIGH, result.getSeverity());
        assertTrue(result.isActive());

        verify(ruleRepository, times(1)).save(any(DataQualityRule.class));
    }

    @Test
    void testGetRule() {
        // Given
        DataQualityRule rule = DataQualityRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name("Test Rule")
                .ruleType(DataQualityRule.RuleType.COMPLETENESS)
                .entityType("Customer")
                .severity(DataQualityRule.RuleSeverity.MEDIUM)
                .active(true)
                .build();

        when(ruleRepository.findById(tenantId, ruleId)).thenReturn(Optional.of(rule));

        // When
        DataQualityRuleDto result = applicationService.getRule(
                com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityRuleQuery.builder()
                        .tenantId(tenantId)
                        .ruleId(ruleId)
                        .build()
        );

        // Then
        assertNotNull(result);
        assertEquals(ruleId, result.getId());
        assertEquals("Test Rule", result.getName());

        verify(ruleRepository, times(1)).findById(tenantId, ruleId);
    }

    @Test
    void testGetRuleNotFound() {
        // Given
        when(ruleRepository.findById(tenantId, ruleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.getRule(
                    com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityRuleQuery.builder()
                            .tenantId(tenantId)
                            .ruleId(ruleId)
                            .build()
            );
        });

        verify(ruleRepository, times(1)).findById(tenantId, ruleId);
    }

    @Test
    void testListRules() {
        // Given
        DataQualityRule rule1 = DataQualityRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name("Rule 1")
                .ruleType(DataQualityRule.RuleType.COMPLETENESS)
                .active(true)
                .build();

        DataQualityRule rule2 = DataQualityRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name("Rule 2")
                .ruleType(DataQualityRule.RuleType.ACCURACY)
                .active(true)
                .build();

        when(ruleRepository.findByTenantId(tenantId)).thenReturn(java.util.List.of(rule1, rule2));

        // When
        var result = applicationService.listRules(
                com.gogidix.rapidassist.ai.dataquality.application.query.ListDataQualityRulesQuery.builder()
                        .tenantId(tenantId)
                        .build()
        );

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Rule 1", result.get(0).getName());
        assertEquals("Rule 2", result.get(1).getName());

        verify(ruleRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    void testDeleteRule() {
        // Given
        DataQualityRule rule = DataQualityRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name("Test Rule")
                .build();

        when(ruleRepository.findById(tenantId, ruleId)).thenReturn(Optional.of(rule));
        doNothing().when(ruleRepository).deleteById(tenantId, ruleId);

        // When
        applicationService.deleteRule(tenantId, ruleId);

        // Then
        verify(ruleRepository, times(1)).deleteById(tenantId, ruleId);
    }

    @Test
    void testUpdateRule() {
        // Given
        UpdateDataQualityRuleCommand command = UpdateDataQualityRuleCommand.builder()
                .tenantId(tenantId)
                .ruleId(ruleId)
                .name("Updated Rule Name")
                .description("Updated description")
                .thresholdValue("new-value")
                .parameters(Map.of("param1", "value1"))
                .updatedBy("admin")
                .build();

        DataQualityRule existingRule = DataQualityRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name("Original Rule")
                .thresholdValue("old-value")
                .parameters(new HashMap<>())
                .build();

        when(ruleRepository.findById(tenantId, ruleId)).thenReturn(Optional.of(existingRule));
        when(ruleRepository.save(any(DataQualityRule.class))).thenReturn(existingRule);

        // When
        DataQualityRuleDto result = applicationService.updateRule(command);

        // Then
        assertNotNull(result);
        verify(ruleRepository).save(any(DataQualityRule.class));
    }

    @Test
    void testUpdateRuleNotFound() {
        // Given
        UpdateDataQualityRuleCommand command = UpdateDataQualityRuleCommand.builder()
                .tenantId(tenantId)
                .ruleId(ruleId)
                .build();

        when(ruleRepository.findById(tenantId, ruleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.updateRule(command);
        });

        verify(ruleRepository, never()).save(any());
    }

    @Test
    void testExecuteCheck() {
        // Given
        UUID checkId = UUID.randomUUID();
        ExecuteDataQualityCheckCommand command = ExecuteDataQualityCheckCommand.builder()
                .tenantId(tenantId)
                .ruleId(ruleId)
                .checkName("Email Validation Check")
                .entityType("Customer")
                .datasetIdentifier("customers_2024.csv")
                .checkParameters(Map.of("sample_size", 1000))
                .executedBy("system")
                .build();

        DataQualityRule rule = DataQualityRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name("Email Validation Rule")
                .ruleType(DataQualityRule.RuleType.VALIDITY)
                .severity(DataQualityRule.RuleSeverity.HIGH)
                .attributeName("email")
                .build();

        DataQualityCheck savedCheck = DataQualityCheck.builder()
                .id(checkId)
                .tenantId(tenantId)
                .ruleId(ruleId)
                .checkName("Email Validation Check")
                .entityType("Customer")
                .status(DataQualityCheck.CheckStatus.RUNNING)
                .build();

        when(ruleRepository.findById(tenantId, ruleId)).thenReturn(Optional.of(rule));
        when(checkRepository.save(any(DataQualityCheck.class))).thenReturn(savedCheck);
        when(checkRepository.findByTenantIdAndRuleId(tenantId, ruleId)).thenReturn(List.of());
        when(issueRepository.save(any(DataQualityIssue.class))).thenReturn(any());
        when(checkRepository.findByTenantIdAndExecutedAtBetween(any(), any(), any())).thenReturn(List.of());
        when(issueRepository.findIssuesDetectedBetween(any(), any(), any())).thenReturn(List.of());

        // When
        DataQualityCheckDto result = applicationService.executeCheck(command);

        // Then
        assertNotNull(result);
        verify(ruleRepository).findById(tenantId, ruleId);
        verify(checkRepository, atLeastOnce()).save(any(DataQualityCheck.class));
    }

    @Test
    void testExecuteCheckRuleNotFound() {
        // Given
        ExecuteDataQualityCheckCommand command = ExecuteDataQualityCheckCommand.builder()
                .tenantId(tenantId)
                .ruleId(ruleId)
                .checkName("Test Check")
                .entityType("Customer")
                .executedBy("system")
                .build();

        when(ruleRepository.findById(tenantId, ruleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.executeCheck(command);
        });

        verify(checkRepository, never()).save(any());
    }

    @Test
    void testGetCheck() {
        // Given
        UUID checkId = UUID.randomUUID();
        DataQualityCheck check = DataQualityCheck.builder()
                .id(checkId)
                .tenantId(tenantId)
                .ruleId(ruleId)
                .checkName("Test Check")
                .status(DataQualityCheck.CheckStatus.COMPLETED)
                .build();

        when(checkRepository.findById(tenantId, checkId)).thenReturn(Optional.of(check));

        // When
        DataQualityCheckDto result = applicationService.getCheck(
                com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityCheckQuery.builder()
                        .tenantId(tenantId)
                        .checkId(checkId)
                        .build()
        );

        // Then
        assertNotNull(result);
        assertEquals(checkId, result.getId());
        verify(checkRepository).findById(tenantId, checkId);
    }

    @Test
    void testGetCheckNotFound() {
        // Given
        UUID checkId = UUID.randomUUID();
        when(checkRepository.findById(tenantId, checkId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.getCheck(
                    com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityCheckQuery.builder()
                            .tenantId(tenantId)
                            .checkId(checkId)
                            .build()
            );
        });

        verify(checkRepository).findById(tenantId, checkId);
    }

    @Test
    void testListChecks() {
        // Given
        UUID checkId1 = UUID.randomUUID();
        UUID checkId2 = UUID.randomUUID();

        DataQualityCheck check1 = DataQualityCheck.builder()
                .id(checkId1)
                .tenantId(tenantId)
                .ruleId(ruleId)
                .checkName("Check 1")
                .build();

        DataQualityCheck check2 = DataQualityCheck.builder()
                .id(checkId2)
                .tenantId(tenantId)
                .ruleId(ruleId)
                .checkName("Check 2")
                .build();

        when(checkRepository.findByTenantIdAndRuleId(tenantId, ruleId))
                .thenReturn(List.of(check1, check2));

        // When
        List<DataQualityCheckDto> result = applicationService.listChecks(tenantId, ruleId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(checkRepository).findByTenantIdAndRuleId(tenantId, ruleId);
    }

    @Test
    void testListIssues() {
        // Given
        UUID issueId1 = UUID.randomUUID();
        UUID issueId2 = UUID.randomUUID();

        DataQualityIssue issue1 = DataQualityIssue.builder()
                .id(issueId1)
                .tenantId(tenantId)
                .severity(DataQualityIssue.IssueSeverity.HIGH)
                .entityType("Customer")
                .status(DataQualityIssue.IssueStatus.OPEN)
                .build();

        DataQualityIssue issue2 = DataQualityIssue.builder()
                .id(issueId2)
                .tenantId(tenantId)
                .severity(DataQualityIssue.IssueSeverity.MEDIUM)
                .entityType("Order")
                .status(DataQualityIssue.IssueStatus.OPEN)
                .build();

        when(issueRepository.findByTenantId(tenantId)).thenReturn(List.of(issue1, issue2));

        // When
        List<DataQualityIssueDto> result = applicationService.listIssues(
                com.gogidix.rapidassist.ai.dataquality.application.query.ListDataQualityIssuesQuery.builder()
                        .tenantId(tenantId)
                        .build()
        );

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(issueRepository).findByTenantId(tenantId);
    }

    @Test
    void testListIssuesWithStatusFilter() {
        // Given
        UUID issueId = UUID.randomUUID();
        DataQualityIssue issue = DataQualityIssue.builder()
                .id(issueId)
                .tenantId(tenantId)
                .severity(DataQualityIssue.IssueSeverity.HIGH)
                .status(DataQualityIssue.IssueStatus.IN_PROGRESS)
                .build();

        when(issueRepository.findByTenantIdAndStatus(tenantId, DataQualityIssue.IssueStatus.IN_PROGRESS))
                .thenReturn(List.of(issue));

        // When
        List<DataQualityIssueDto> result = applicationService.listIssues(
                com.gogidix.rapidassist.ai.dataquality.application.query.ListDataQualityIssuesQuery.builder()
                        .tenantId(tenantId)
                        .status(DataQualityIssue.IssueStatus.IN_PROGRESS)
                        .build()
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(issueRepository).findByTenantIdAndStatus(tenantId, DataQualityIssue.IssueStatus.IN_PROGRESS);
    }

    @Test
    void testResolveIssue() {
        // Given
        UUID issueId = UUID.randomUUID();
        DataQualityIssue issue = DataQualityIssue.builder()
                .id(issueId)
                .tenantId(tenantId)
                .status(DataQualityIssue.IssueStatus.OPEN)
                .severity(DataQualityIssue.IssueSeverity.HIGH)
                .build();

        ResolveDataQualityIssueCommand command = ResolveDataQualityIssueCommand.builder()
                .tenantId(tenantId)
                .issueId(issueId)
                .resolvedBy("admin")
                .resolutionNotes("Fixed the data")
                .build();

        when(issueRepository.findById(tenantId, issueId)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(DataQualityIssue.class))).thenReturn(issue);

        // When
        DataQualityIssueDto result = applicationService.resolveIssue(command);

        // Then
        assertNotNull(result);
        verify(issueRepository).save(any(DataQualityIssue.class));
    }

    @Test
    void testResolveIssueNotFound() {
        // Given
        UUID issueId = UUID.randomUUID();
        ResolveDataQualityIssueCommand command = ResolveDataQualityIssueCommand.builder()
                .tenantId(tenantId)
                .issueId(issueId)
                .resolvedBy("admin")
                .build();

        when(issueRepository.findById(tenantId, issueId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.resolveIssue(command);
        });

        verify(issueRepository, never()).save(any());
    }

    @Test
    void testGenerateReport() {
        // Given
        UUID reportId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        GenerateDataQualityReportCommand command = GenerateDataQualityReportCommand.builder()
                .tenantId(tenantId)
                .reportName("Weekly Quality Report")
                .reportType("SUMMARY")
                .reportPeriodStart(startDate)
                .reportPeriodEnd(endDate)
                .generatedBy("admin")
                .build();

        DataQualityReport savedReport = DataQualityReport.builder()
                .id(reportId)
                .tenantId(tenantId)
                .reportName("Weekly Quality Report")
                .status(DataQualityReport.ReportStatus.GENERATING)
                .build();

        when(reportRepository.save(any(DataQualityReport.class))).thenReturn(savedReport);
        when(checkRepository.findByTenantIdAndExecutedAtBetween(eq(tenantId), any(), any()))
                .thenReturn(List.of());
        when(issueRepository.findIssuesDetectedBetween(eq(tenantId), any(), any()))
                .thenReturn(List.of());

        // When
        DataQualityReportDto result = applicationService.generateReport(command);

        // Then
        assertNotNull(result);
        verify(reportRepository, atLeastOnce()).save(any(DataQualityReport.class));
    }

    @Test
    void testGetMetrics() {
        // Given
        UUID metricId = UUID.randomUUID();
        DataQualityMetric metric = DataQualityMetric.builder()
                .id(metricId)
                .tenantId(tenantId)
                .metricName("completeness_score")
                .entityType("Customer")
                .metricValue(95.5)
                .metricTimestamp(LocalDateTime.now())
                .build();

        when(metricRepository.findRecentMetricsByTenantId(eq(tenantId), anyInt()))
                .thenReturn(List.of(metric));

        // When
        List<DataQualityMetricDto> result = applicationService.getMetrics(
                com.gogidix.rapidassist.ai.dataquality.application.query.GetDataQualityMetricsQuery.builder()
                        .tenantId(tenantId)
                        .limit(10)
                        .build()
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(metricRepository).findRecentMetricsByTenantId(tenantId, 10);
    }

    @Test
    void testListRulesWithFilters() {
        // Given - Filter by rule type
        DataQualityRule rule1 = DataQualityRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name("Validity Rule")
                .ruleType(DataQualityRule.RuleType.VALIDITY)
                .active(true)
                .build();

        when(ruleRepository.findByTenantIdAndRuleType(tenantId, DataQualityRule.RuleType.VALIDITY))
                .thenReturn(List.of(rule1));

        // When
        var result = applicationService.listRules(
                com.gogidix.rapidassist.ai.dataquality.application.query.ListDataQualityRulesQuery.builder()
                        .tenantId(tenantId)
                        .ruleType(DataQualityRule.RuleType.VALIDITY)
                        .build()
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(DataQualityRule.RuleType.VALIDITY, result.get(0).getRuleType());
        verify(ruleRepository).findByTenantIdAndRuleType(tenantId, DataQualityRule.RuleType.VALIDITY);
    }
}
