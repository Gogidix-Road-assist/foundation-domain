package com.gogidix.rapidassist.ai.dataquality.application.service;

import com.gogidix.rapidassist.ai.dataquality.application.command.*;
import com.gogidix.rapidassist.ai.dataquality.application.dto.*;
import com.gogidix.rapidassist.ai.dataquality.application.mapper.DataQualityMapper;
import com.gogidix.rapidassist.ai.dataquality.application.query.*;
import com.gogidix.rapidassist.ai.dataquality.domain.model.*;
import com.gogidix.rapidassist.ai.dataquality.domain.repository.*;
import com.gogidix.rapidassist.shared.request.context.library.domain.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service for Data Quality operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataQualityApplicationService {

    private final DataQualityRuleRepositoryPort ruleRepository;
    private final DataQualityCheckRepositoryPort checkRepository;
    private final DataQualityIssueRepositoryPort issueRepository;
    private final DataQualityReportRepositoryPort reportRepository;
    private final DataQualityMetricRepositoryPort metricRepository;
    private final DataQualityMapper mapper;

    // ==================== Rule Operations ====================

    @Transactional
    public DataQualityRuleDto createRule(CreateDataQualityRuleCommand command) {
        log.info("Creating data quality rule: {} for tenant: {}", command.getName(), command.getTenantId());

        // Tenant context is set by interceptors, no need to set here

        DataQualityRule rule = DataQualityRule.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .name(command.getName())
                .description(command.getDescription())
                .ruleType(command.getRuleType())
                .entityType(command.getEntityType())
                .attributeName(command.getAttributeName())
                .operator(command.getOperator())
                .thresholdValue(command.getThresholdValue())
                .parameters(command.getParameters())
                .severity(command.getSeverity())
                .active(command.isActive())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(command.getCreatedBy())
                .build();

        DataQualityRule savedRule = ruleRepository.save(rule);
        log.info("Created rule with ID: {}", savedRule.getId());

        return mapper.toDto(savedRule);
    }

    @Transactional
    public DataQualityRuleDto updateRule(UpdateDataQualityRuleCommand command) {
        log.info("Updating data quality rule: {} for tenant: {}", command.getRuleId(), command.getTenantId());

        // Tenant context is set by interceptors, no need to set here

        DataQualityRule rule = ruleRepository.findById(command.getTenantId(), command.getRuleId())
                .orElseThrow(() -> new IllegalArgumentException("Rule not found"));

        if (command.getName() != null) {
            rule.setName(command.getName());
        }
        if (command.getDescription() != null) {
            rule.setDescription(command.getDescription());
        }
        if (command.getThresholdValue() != null) {
            rule.updateThreshold(command.getThresholdValue());
        }
        if (command.getParameters() != null) {
            rule.updateParameters(command.getParameters());
        }
        rule.setUpdatedBy(command.getUpdatedBy());
        rule.setUpdatedAt(LocalDateTime.now());

        DataQualityRule updatedRule = ruleRepository.save(rule);
        log.info("Updated rule: {}", updatedRule.getId());

        return mapper.toDto(updatedRule);
    }

    @Transactional
    public void deleteRule(String tenantId, UUID ruleId) {
        log.info("Deleting data quality rule: {} for tenant: {}", ruleId, tenantId);

        // Tenant context is set by filters/interceptors
        ruleRepository.deleteById(tenantId, ruleId);

        log.info("Deleted rule: {}", ruleId);
    }

    public DataQualityRuleDto getRule(GetDataQualityRuleQuery query) {
        log.info("Getting rule: {} for tenant: {}", query.getRuleId(), query.getTenantId());

        // Tenant context is set by filters/interceptors

        DataQualityRule rule = ruleRepository.findById(query.getTenantId(), query.getRuleId())
                .orElseThrow(() -> new IllegalArgumentException("Rule not found"));

        return mapper.toDto(rule);
    }

    public List<DataQualityRuleDto> listRules(ListDataQualityRulesQuery query) {
        log.info("Listing rules for tenant: {}", query.getTenantId());

        // Tenant context is set by filters/interceptors

        List<DataQualityRule> rules;
        if (query.getRuleType() != null) {
            rules = ruleRepository.findByTenantIdAndRuleType(query.getTenantId(), query.getRuleType());
        } else if (query.getEntityType() != null) {
            rules = ruleRepository.findByTenantIdAndEntityType(query.getTenantId(), query.getEntityType());
        } else if (query.getSeverity() != null) {
            rules = ruleRepository.findByTenantIdAndSeverity(query.getTenantId(), query.getSeverity());
        } else if (query.getActive() != null) {
            rules = ruleRepository.findByTenantIdAndActive(query.getTenantId(), query.getActive());
        } else {
            rules = ruleRepository.findByTenantId(query.getTenantId());
        }

        return mapper.toRuleDtoList(rules);
    }

    // ==================== Check Operations ====================

    @Transactional
    public DataQualityCheckDto executeCheck(ExecuteDataQualityCheckCommand command) {
        log.info("Executing data quality check for rule: {} tenant: {}", command.getRuleId(), command.getTenantId());

        // Tenant context is set by interceptors, no need to set here

        DataQualityRule rule = ruleRepository.findById(command.getTenantId(), command.getRuleId())
                .orElseThrow(() -> new IllegalArgumentException("Rule not found"));

        DataQualityCheck check = DataQualityCheck.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .ruleId(command.getRuleId())
                .checkName(command.getCheckName())
                .entityType(command.getEntityType())
                .datasetIdentifier(command.getDatasetIdentifier())
                .status(DataQualityCheck.CheckStatus.PENDING)
                .checkParameters(command.getCheckParameters())
                .executedBy(command.getExecutedBy())
                .build();

        check.start();
        DataQualityCheck savedCheck = checkRepository.save(check);

        // Simulate check execution (in real implementation, this would run actual validation)
        simulateCheckExecution(savedCheck, rule);

        log.info("Completed check: {} with status: {}", savedCheck.getId(), savedCheck.getStatus());
        return mapper.toDto(savedCheck);
    }

    public DataQualityCheckDto getCheck(GetDataQualityCheckQuery query) {
        log.info("Getting check: {} for tenant: {}", query.getCheckId(), query.getTenantId());

        // Tenant context is set by filters/interceptors

        DataQualityCheck check = checkRepository.findById(query.getTenantId(), query.getCheckId())
                .orElseThrow(() -> new IllegalArgumentException("Check not found"));

        return mapper.toDto(check);
    }

    public List<DataQualityCheckDto> listChecks(String tenantId, UUID ruleId) {
        log.info("Listing checks for rule: {} tenant: {}", ruleId, tenantId);

        // Tenant context is set by filters/interceptors

        List<DataQualityCheck> checks = checkRepository.findByTenantIdAndRuleId(tenantId, ruleId);
        return mapper.toCheckDtoList(checks);
    }

    // ==================== Issue Operations ====================

    public List<DataQualityIssueDto> listIssues(ListDataQualityIssuesQuery query) {
        log.info("Listing issues for tenant: {}", query.getTenantId());

        // Tenant context is set by filters/interceptors

        List<DataQualityIssue> issues;
        if (query.getStatus() != null) {
            issues = issueRepository.findByTenantIdAndStatus(query.getTenantId(), query.getStatus());
        } else if (query.getSeverity() != null) {
            issues = issueRepository.findByTenantIdAndSeverity(query.getTenantId(), query.getSeverity());
        } else if (query.getEntityType() != null) {
            issues = issueRepository.findByTenantIdAndEntityType(query.getTenantId(), query.getEntityType());
        } else {
            issues = issueRepository.findByTenantId(query.getTenantId());
        }

        return mapper.toIssueDtoList(issues);
    }

    @Transactional
    public DataQualityIssueDto resolveIssue(ResolveDataQualityIssueCommand command) {
        log.info("Resolving issue: {} for tenant: {}", command.getIssueId(), command.getTenantId());

        // Tenant context is set by interceptors, no need to set here

        DataQualityIssue issue = issueRepository.findById(command.getTenantId(), command.getIssueId())
                .orElseThrow(() -> new IllegalArgumentException("Issue not found"));

        issue.resolve(command.getResolvedBy(), command.getResolutionNotes());
        DataQualityIssue savedIssue = issueRepository.save(issue);

        log.info("Resolved issue: {}", savedIssue.getId());
        return mapper.toDto(savedIssue);
    }

    // ==================== Report Operations ====================

    @Transactional
    public DataQualityReportDto generateReport(GenerateDataQualityReportCommand command) {
        log.info("Generating data quality report for tenant: {}", command.getTenantId());

        // Tenant context is set by interceptors, no need to set here

        DataQualityReport report = DataQualityReport.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .reportName(command.getReportName())
                .reportType(command.getReportType())
                .reportPeriodStart(command.getReportPeriodStart())
                .reportPeriodEnd(command.getReportPeriodEnd())
                .status(DataQualityReport.ReportStatus.GENERATING)
                .generatedBy(command.getGeneratedBy())
                .build();

        report.startGeneration();
        DataQualityReport savedReport = reportRepository.save(report);

        // Generate report data
        generateReportData(savedReport, command.getTenantId(), command.getReportPeriodStart(), command.getReportPeriodEnd());

        savedReport.complete();
        DataQualityReport updatedReport = reportRepository.save(savedReport);

        log.info("Generated report: {} with score: {}", updatedReport.getId(), updatedReport.getOverallQualityScore());
        return mapper.toDto(updatedReport);
    }

    // ==================== Metrics Operations ====================

    public List<DataQualityMetricDto> getMetrics(GetDataQualityMetricsQuery query) {
        log.info("Getting metrics for tenant: {}", query.getTenantId());

        // Tenant context is set by filters/interceptors

        List<DataQualityMetric> metrics;
        if (query.getMetricName() != null && query.getEntityType() != null) {
            DataQualityMetric metric = metricRepository
                    .findLatestByTenantIdAndMetricNameAndEntityType(
                            query.getTenantId(),
                            query.getMetricName(),
                            query.getEntityType()
                    )
                    .orElse(null);
            return metric != null ? List.of(mapper.toDto(metric)) : List.of();
        } else if (query.getMetricName() != null) {
            metrics = metricRepository.findLatestMetricsByTenantIdAndMetricName(
                    query.getTenantId(),
                    query.getMetricName(),
                    query.getLimit() != null ? query.getLimit() : 10
            );
        } else if (query.getStartDate() != null && query.getEndDate() != null) {
            metrics = metricRepository.findByTenantIdAndMetricTimestampBetween(
                    query.getTenantId(),
                    query.getStartDate(),
                    query.getEndDate()
            );
        } else {
            metrics = metricRepository.findRecentMetricsByTenantId(
                    query.getTenantId(),
                    query.getLimit() != null ? query.getLimit() : 50
            );
        }

        return mapper.toMetricDtoList(metrics);
    }

    // ==================== Helper Methods ====================

    private void simulateCheckExecution(DataQualityCheck check, DataQualityRule rule) {
        // Simulate validation logic
        int totalRecords = 1000;
        int passedRecords = (int) (Math.random() * 200) + 800; // 800-999 passed
        int failedRecords = totalRecords - passedRecords;

        check.complete(passedRecords, failedRecords, totalRecords);
        checkRepository.save(check);

        // Create issues for failed records
        if (failedRecords > 0) {
            for (int i = 0; i < Math.min(failedRecords, 10); i++) {
                DataQualityIssue.IssueSeverity issueSeverity = switch (rule.getSeverity()) {
                    case CRITICAL -> DataQualityIssue.IssueSeverity.CRITICAL;
                    case HIGH -> DataQualityIssue.IssueSeverity.HIGH;
                    case MEDIUM -> DataQualityIssue.IssueSeverity.MEDIUM;
                    case LOW -> DataQualityIssue.IssueSeverity.LOW;
                    default -> DataQualityIssue.IssueSeverity.LOW;
                };

                DataQualityIssue issue = DataQualityIssue.builder()
                        .id(UUID.randomUUID())
                        .tenantId(check.getTenantId())
                        .checkId(check.getId())
                        .ruleId(rule.getId())
                        .issueType(rule.getRuleType().name())
                        .severity(issueSeverity)
                        .entityType(check.getEntityType())
                        .entityId(UUID.randomUUID().toString())
                        .attributeName(rule.getAttributeName())
                        .description("Data quality issue detected")
                        .status(DataQualityIssue.IssueStatus.OPEN)
                        .detectedAt(LocalDateTime.now())
                        .occurrenceCount(1)
                        .build();

                issueRepository.save(issue);
            }
        }
    }

    private void generateReportData(DataQualityReport report, String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        List<DataQualityCheck> checks = checkRepository.findByTenantIdAndExecutedAtBetween(tenantId, startDate, endDate);

        int totalChecks = checks.size();
        int passedChecks = (int) checks.stream().filter(DataQualityCheck::isPassed).count();
        int failedChecks = totalChecks - passedChecks;

        List<DataQualityIssue> issues = issueRepository.findIssuesDetectedBetween(tenantId, startDate, endDate);
        int criticalIssues = (int) issues.stream().filter(i -> i.getSeverity() == DataQualityIssue.IssueSeverity.CRITICAL).count();
        int highIssues = (int) issues.stream().filter(i -> i.getSeverity() == DataQualityIssue.IssueSeverity.HIGH).count();
        int mediumIssues = (int) issues.stream().filter(i -> i.getSeverity() == DataQualityIssue.IssueSeverity.MEDIUM).count();
        int lowIssues = (int) issues.stream().filter(i -> i.getSeverity() == DataQualityIssue.IssueSeverity.LOW).count();

        report.updateStatistics(totalChecks, passedChecks, failedChecks);
        report.updateIssueCounts(criticalIssues, highIssues, mediumIssues, lowIssues);
    }
}
