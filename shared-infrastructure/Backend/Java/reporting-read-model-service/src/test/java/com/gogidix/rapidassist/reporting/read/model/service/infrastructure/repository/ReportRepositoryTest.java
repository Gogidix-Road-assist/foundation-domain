package com.gogidix.rapidassist.reporting.read.model.service.infrastructure.repository;

import com.gogidix.rapidassist.reporting.read.model.service.domain.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CRITICAL tenant isolation tests for ReportRepository.
 * <p>
 * These tests verify that NO cross-tenant data access is possible.
 * Reports are READ-ONLY views that aggregate data - without tenant filtering,
 * Tenant A could see ALL of Tenant B's data - CATASTROPHIC.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ReportRepository - CRITICAL Tenant Isolation Tests")
class ReportRepositoryTest {

    private static final String TENANT_1 = "tenant-1";
    private static final String TENANT_2 = "tenant-2";
    private static final String REPORT_TYPE = "usage-summary";
    private static final String GENERATED_BY = "user-123";

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Report tenant1Report1;
    private Report tenant1Report2;
    private Report tenant2Report1;

    @BeforeEach
    void setUp() {
        reportRepository.deleteAll();

        // Create reports for tenant 1
        tenant1Report1 = Report.builder()
                .tenantId(TENANT_1)
                .reportType(REPORT_TYPE)
                .title("Tenant 1 Usage Report")
                .generatedBy(GENERATED_BY)
                .status(Report.ReportStatus.COMPLETED)
                .build();

        tenant1Report2 = Report.builder()
                .tenantId(TENANT_1)
                .reportType("billing-summary")
                .title("Tenant 1 Billing Report")
                .generatedBy(GENERATED_BY)
                .status(Report.ReportStatus.PENDING)
                .build();

        // Create report for tenant 2
        tenant2Report1 = Report.builder()
                .tenantId(TENANT_2)
                .reportType(REPORT_TYPE)
                .title("Tenant 2 Usage Report")
                .generatedBy(GENERATED_BY)
                .status(Report.ReportStatus.COMPLETED)
                .build();

        tenant1Report1 = reportRepository.save(tenant1Report1);
        tenant1Report2 = reportRepository.save(tenant1Report2);
        tenant2Report1 = reportRepository.save(tenant2Report1);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findByTenantId - Should only return reports for specified tenant")
    void findByTenantId_ShouldOnlyReturnReportsForTenant() {
        // When
        List<Report> tenant1Reports = reportRepository.findByTenantId(TENANT_1);
        List<Report> tenant2Reports = reportRepository.findByTenantId(TENANT_2);

        // Then
        assertThat(tenant1Reports).hasSize(2);
        assertThat(tenant1Reports)
                .allMatch(r -> r.getTenantId().equals(TENANT_1));

        assertThat(tenant2Reports).hasSize(1);
        assertThat(tenant2Reports)
                .allMatch(r -> r.getTenantId().equals(TENANT_2));
    }

    @Test
    @DisplayName("findByIdAndTenantId - Should only return report if both ID and tenant match")
    void findByIdAndTenantId_ShouldRequireBothIdAndTenantMatch() {
        // When
        Optional<Report> foundForTenant1 = reportRepository.findByIdAndTenantId(
                tenant1Report1.getId(), TENANT_1
        );
        Optional<Report> notFoundForTenant2 = reportRepository.findByIdAndTenantId(
                tenant1Report1.getId(), TENANT_2
        );

        // Then
        assertThat(foundForTenant1).isPresent();
        assertThat(foundForTenant1.get().getId()).isEqualTo(tenant1Report1.getId());

        // CRITICAL: Tenant 2 cannot access tenant 1's report
        assertThat(notFoundForTenant2).isEmpty();
    }

    @Test
    @DisplayName("findByTenantIdAndReportType - Should filter by both tenant and type")
    void findByTenantIdAndReportType_ShouldFilterByTenantAndType() {
        // When
        List<Report> tenant1Usage = reportRepository.findByTenantIdAndReportType(TENANT_1, REPORT_TYPE);
        List<Report> tenant2Usage = reportRepository.findByTenantIdAndReportType(TENANT_2, REPORT_TYPE);
        List<Report> tenant1Billing = reportRepository.findByTenantIdAndReportType(TENANT_1, "billing-summary");

        // Then
        assertThat(tenant1Usage).hasSize(1);
        assertThat(tenant1Usage.get(0).getId()).isEqualTo(tenant1Report1.getId());

        assertThat(tenant2Usage).hasSize(1);
        assertThat(tenant2Usage.get(0).getId()).isEqualTo(tenant2Report1.getId());

        assertThat(tenant1Billing).hasSize(1);
        assertThat(tenant1Billing.get(0).getId()).isEqualTo(tenant1Report2.getId());
    }

    @Test
    @DisplayName("findByTenantIdAndStatus - Should filter by both tenant and status")
    void findByTenantIdAndStatus_ShouldFilterByTenantAndStatus() {
        // When
        List<Report> tenant1Completed = reportRepository.findByTenantIdAndStatus(
                TENANT_1, Report.ReportStatus.COMPLETED
        );
        List<Report> tenant2Completed = reportRepository.findByTenantIdAndStatus(
                TENANT_2, Report.ReportStatus.COMPLETED
        );
        List<Report> tenant1Pending = reportRepository.findByTenantIdAndStatus(
                TENANT_1, Report.ReportStatus.PENDING
        );

        // Then
        assertThat(tenant1Completed).hasSize(1);
        assertThat(tenant1Completed.get(0).getId()).isEqualTo(tenant1Report1.getId());

        assertThat(tenant2Completed).hasSize(1);
        assertThat(tenant2Completed.get(0).getId()).isEqualTo(tenant2Report1.getId());

        assertThat(tenant1Pending).hasSize(1);
        assertThat(tenant1Pending.get(0).getId()).isEqualTo(tenant1Report2.getId());
    }

    @Test
    @DisplayName("findByTenantIdAndReportTypeAndStatus - Should filter by tenant, type, and status")
    void findByTenantIdAndReportTypeAndStatus_ShouldFilterByAllThree() {
        // When
        List<Report> tenant1UsageCompleted = reportRepository.findByTenantIdAndReportTypeAndStatus(
                TENANT_1, REPORT_TYPE, Report.ReportStatus.COMPLETED
        );
        List<Report> tenant2UsageCompleted = reportRepository.findByTenantIdAndReportTypeAndStatus(
                TENANT_2, REPORT_TYPE, Report.ReportStatus.COMPLETED
        );

        // Then
        assertThat(tenant1UsageCompleted).hasSize(1);
        assertThat(tenant1UsageCompleted.get(0).getId()).isEqualTo(tenant1Report1.getId());

        assertThat(tenant2UsageCompleted).hasSize(1);
        assertThat(tenant2UsageCompleted.get(0).getId()).isEqualTo(tenant2Report1.getId());
    }

    @Test
    @DisplayName("findByTenantIdAndCreatedAtBetween - Should filter by tenant and date range")
    void findByTenantIdAndCreatedAtBetween_ShouldFilterByTenant() {
        // When
        List<Report> tenant1Reports = reportRepository.findByTenantIdAndCreatedAtBetween(
                TENANT_1,
                tenant1Report1.getCreatedAt().minusSeconds(60),
                tenant1Report1.getCreatedAt().plusSeconds(60)
        );

        // Then
        assertThat(tenant1Reports).hasSize(2);
        assertThat(tenant1Reports)
                .allMatch(r -> r.getTenantId().equals(TENANT_1));
    }

    @Test
    @DisplayName("findByTenantIdAndGeneratedBy - Should filter by both tenant and generator")
    void findByTenantIdAndGeneratedBy_ShouldFilterByTenantAndGenerator() {
        // When
        List<Report> tenant1Reports = reportRepository.findByTenantIdAndGeneratedBy(TENANT_1, GENERATED_BY);
        List<Report> tenant2Reports = reportRepository.findByTenantIdAndGeneratedBy(TENANT_2, GENERATED_BY);

        // Then
        assertThat(tenant1Reports).hasSize(2);
        assertThat(tenant2Reports).hasSize(1);
    }

    @Test
    @DisplayName("findActiveByTenantId - Should only return active reports for tenant")
    void findActiveByTenantId_ShouldFilterByTenant() {
        // When
        List<Report> tenant1Active = reportRepository.findActiveByTenantId(TENANT_1, Instant.now());
        List<Report> tenant2Active = reportRepository.findActiveByTenantId(TENANT_2, Instant.now());

        // Then
        assertThat(tenant1Active).hasSize(2);
        assertThat(tenant2Active).hasSize(1);
    }

    @Test
    @DisplayName("findReadyByTenantId - Should only return ready reports for tenant")
    void findReadyByTenantId_ShouldFilterByTenant() {
        // When
        List<Report> tenant1Ready = reportRepository.findReadyByTenantId(TENANT_1, Instant.now());
        List<Report> tenant2Ready = reportRepository.findReadyByTenantId(TENANT_2, Instant.now());

        // Then
        assertThat(tenant1Ready).hasSize(1);
        assertThat(tenant1Ready.get(0).getId()).isEqualTo(tenant1Report1.getId());

        assertThat(tenant2Ready).hasSize(1);
        assertThat(tenant2Ready.get(0).getId()).isEqualTo(tenant2Report1.getId());
    }

    @Test
    @DisplayName("countByTenantIdAndStatus - Should count only for specified tenant")
    void countByTenantIdAndStatus_ShouldCountOnlyForTenant() {
        // When
        long tenant1CompletedCount = reportRepository.countByTenantIdAndStatus(
                TENANT_1, Report.ReportStatus.COMPLETED
        );
        long tenant2CompletedCount = reportRepository.countByTenantIdAndStatus(
                TENANT_2, Report.ReportStatus.COMPLETED
        );

        // Then
        assertThat(tenant1CompletedCount).isEqualTo(1);
        assertThat(tenant2CompletedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("countByTenantIdAndReportType - Should count only for specified tenant")
    void countByTenantIdAndReportType_ShouldCountOnlyForTenant() {
        // When
        long tenant1UsageCount = reportRepository.countByTenantIdAndReportType(TENANT_1, REPORT_TYPE);
        long tenant2UsageCount = reportRepository.countByTenantIdAndReportType(TENANT_2, REPORT_TYPE);

        // Then
        assertThat(tenant1UsageCount).isEqualTo(1);
        assertThat(tenant2UsageCount).isEqualTo(1);
    }

    @Test
    @DisplayName("deleteByIdAndTenantId - Should only delete if both ID and tenant match")
    void deleteByIdAndTenantId_ShouldRequireTenantMatch() {
        // When
        reportRepository.deleteByIdAndTenantId(tenant1Report1.getId(), TENANT_1);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Report> deletedForTenant1 = reportRepository.findByIdAndTenantId(
                tenant1Report1.getId(), TENANT_1
        );
        assertThat(deletedForTenant1).isEmpty();

        // Verify tenant 2's report still exists
        Optional<Report> stillExistsForTenant2 = reportRepository.findByIdAndTenantId(
                tenant2Report1.getId(), TENANT_2
        );
        assertThat(stillExistsForTenant2).isPresent();
    }

    @Test
    @DisplayName("deleteAllByTenantId - Should only delete reports for specified tenant")
    void deleteAllByTenantId_ShouldOnlyDeleteForTenant() {
        // When
        reportRepository.deleteAllByTenantId(TENANT_1);
        entityManager.flush();
        entityManager.clear();

        // Then
        List<Report> tenant1Reports = reportRepository.findByTenantId(TENANT_1);
        List<Report> tenant2Reports = reportRepository.findByTenantId(TENANT_2);

        assertThat(tenant1Reports).isEmpty();
        assertThat(tenant2Reports).hasSize(1);
    }

    @Test
    @DisplayName("existsByIdAndTenantId - Should check both ID and tenant")
    void existsByIdAndTenantId_ShouldCheckBothIdAndTenant() {
        // When
        boolean existsForTenant1 = reportRepository.existsByIdAndTenantId(
                tenant1Report1.getId(), TENANT_1
        );
        boolean notExistsForTenant2 = reportRepository.existsByIdAndTenantId(
                tenant1Report1.getId(), TENANT_2
        );

        // Then
        assertThat(existsForTenant1).isTrue();
        assertThat(notExistsForTenant2).isFalse();
    }

    @Test
    @DisplayName("findAll - Should be BLOCKED to prevent CATASTROPHIC cross-tenant data access")
    void findAll_ShouldBeBlocked() {
        // When & Then
        assertThatThrownBy(() -> reportRepository.findAll())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("CRITICAL")
                .hasMessageContaining("CATASTROPHIC")
                .hasMessageContaining("data leakage");
    }

    @Test
    @DisplayName("findAllById - Should be BLOCKED to prevent cross-tenant access")
    void findAllById_ShouldBeBlocked() {
        // When & Then
        assertThatThrownBy(() -> reportRepository.findAllById(List.of(tenant1Report1.getId())))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("CRITICAL");
    }

    @Test
    @DisplayName("deleteAll - Should be BLOCKED to prevent mass deletion")
    void deleteAll_ShouldBeBlocked() {
        // When & Then
        assertThatThrownBy(() -> reportRepository.deleteAll())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("CRITICAL");
    }

    @Test
    @DisplayName("deleteAllById - Should be BLOCKED to prevent mass deletion")
    void deleteAllById_ShouldBeBlocked() {
        // When & Then
        assertThatThrownBy(() -> reportRepository.deleteAllById(List.of(tenant1Report1.getId())))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("CRITICAL");
    }

    @Test
    @DisplayName("CRITICAL: Tenant 2 cannot access any of Tenant 1's reports by any method")
    void critical_Tenant2CannotAccessTenant1Reports() {
        // Test all possible access methods
        assertThat(reportRepository.findByTenantId(TENANT_2))
                .noneMatch(r -> r.getTenantId().equals(TENANT_1));

        assertThat(reportRepository.findByIdAndTenantId(tenant1Report1.getId(), TENANT_2))
                .isEmpty();

        assertThat(reportRepository.findByTenantIdAndReportType(TENANT_2, REPORT_TYPE))
                .noneMatch(r -> r.getId().equals(tenant1Report1.getId()));

        assertThat(reportRepository.findByTenantIdAndStatus(TENANT_2, Report.ReportStatus.COMPLETED))
                .noneMatch(r -> r.getId().equals(tenant1Report1.getId()));

        assertThat(reportRepository.existsByIdAndTenantId(tenant1Report1.getId(), TENANT_2))
                .isFalse();

        // Verify counts are separate
        assertThat(reportRepository.findByTenantId(TENANT_1)).hasSize(2);
        assertThat(reportRepository.findByTenantId(TENANT_2)).hasSize(1);
    }

    @Test
    @DisplayName("CRITICAL: Verify report data isolation prevents data leakage")
    void critical_ReportDataIsolationPreventsDataLeakage() {
        // Given: Tenant 1 has sensitive report data
        Report sensitiveReport = Report.builder()
                .tenantId(TENANT_1)
                .reportType("financial-summary")
                .title("Q4 Financial Results")
                .generatedBy("ceo")
                .status(Report.ReportStatus.COMPLETED)
                .data("{\"revenue\": 1000000, \"profit\": 500000}")
                .build();
        reportRepository.save(sensitiveReport);

        // When: Tenant 2 tries to access reports
        List<Report> tenant2Reports = reportRepository.findByTenantId(TENANT_2);
        List<Report> allTenant1Reports = reportRepository.findByTenantId(TENANT_1);

        // Then: Verify complete isolation
        assertThat(tenant2Reports)
                .hasSize(1)
                .noneMatch(r -> r.getTenantId().equals(TENANT_1));

        assertThat(allTenant1Reports)
                .hasSize(3)
                .anyMatch(r -> r.getId().equals(sensitiveReport.getId()));

        // Verify tenant 2 cannot access the sensitive report by any means
        assertThat(reportRepository.findByIdAndTenantId(sensitiveReport.getId(), TENANT_2))
                .isEmpty();
    }
}
