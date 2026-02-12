package com.gogidix.rapidassist.orchestration.reporting.domain.port.in;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportSubscription;

import java.util.List;
import java.util.Optional;

/**
 * Input port for Report Subscription operations.
 * Defines the contract for subscription management use cases.
 */
public interface ReportSubscriptionPort {

    /**
     * Create a new subscription
     */
    ReportSubscription subscribe(ReportSubscription subscription);

    /**
     * Update subscription
     */
    ReportSubscription updateSubscription(String subscriptionId, ReportSubscription subscription);

    /**
     * Get subscription by ID
     */
    Optional<ReportSubscription> getSubscription(String subscriptionId);

    /**
     * Get subscriptions by tenant
     */
    List<ReportSubscription> getSubscriptionsByTenant(String tenantId);

    /**
     * Get subscriptions by user
     */
    List<ReportSubscription> getSubscriptionsByUser(String userId);

    /**
     * Get subscriptions by report
     */
    List<ReportSubscription> getSubscriptionsByReport(String reportId);

    /**
     * Get subscriptions by schedule
     */
    List<ReportSubscription> getSubscriptionsBySchedule(String scheduleId);

    /**
     * Get active subscriptions
     */
    List<ReportSubscription> getActiveSubscriptionsByTenant(String tenantId);

    /**
     * Delete subscription
     */
    void deleteSubscription(String subscriptionId);

    /**
     * Unsubscribe from report
     */
    void unsubscribe(String subscriptionId);

    /**
     * Activate subscription
     */
    void activateSubscription(String subscriptionId);

    /**
     * Deactivate subscription
     */
    void deactivateSubscription(String subscriptionId);

    /**
     * Get subscribers for report
     */
    List<ReportSubscription> getSubscribersForReport(String reportId);

    /**
     * Get subscribers for schedule
     */
    List<ReportSubscription> getSubscribersForSchedule(String scheduleId);

    /**
     * Record delivery
     */
    void recordDelivery(String subscriptionId);

    /**
     * Get subscription statistics
     */
    SubscriptionStatistics getSubscriptionStatistics(String tenantId);

    record SubscriptionStatistics(
        long totalSubscriptions,
        long activeSubscriptions,
        long unsubscribed,
        long totalDeliveries
    ) {}
}
