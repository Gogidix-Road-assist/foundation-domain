package com.gogidix.rapidassist.orchestration.reporting.domain.port.out;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportSubscription;

import java.util.List;
import java.util.Optional;

/**
 * Output port for ReportSubscription repository operations.
 * Defines the contract for subscription persistence.
 */
public interface ReportSubscriptionRepositoryPort {

    ReportSubscription save(ReportSubscription subscription);

    Optional<ReportSubscription> findBySubscriptionId(String subscriptionId);

    List<ReportSubscription> findByTenantId(String tenantId);

    List<ReportSubscription> findByUserId(String userId);

    List<ReportSubscription> findByReportId(String reportId);

    List<ReportSubscription> findByScheduleId(String scheduleId);

    List<ReportSubscription> findByTenantIdAndStatus(String tenantId, ReportSubscription.SubscriptionStatus status);

    List<ReportSubscription> findActiveSubscriptionsByReportId(String reportId);

    List<ReportSubscription> findActiveSubscriptionsByScheduleId(String scheduleId);

    void deleteBySubscriptionId(String subscriptionId);

    void deleteByReportId(String reportId);

    void deleteByScheduleId(String scheduleId);

    void deleteAllByTenantId(String tenantId);

    boolean existsBySubscriptionId(String subscriptionId);

    long countByTenantId(String tenantId);

    long countByUserId(String userId);

    long countByReportId(String reportId);
}
