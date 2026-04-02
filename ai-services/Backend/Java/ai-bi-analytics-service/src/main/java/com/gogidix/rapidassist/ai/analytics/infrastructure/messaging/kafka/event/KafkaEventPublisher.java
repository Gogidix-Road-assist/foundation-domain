package com.gogidix.rapidassist.ai.analytics.infrastructure.messaging.kafka.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.analytics.domain.model.AnalyticsReport;
import com.gogidix.rapidassist.ai.analytics.domain.model.Dashboard;
import com.gogidix.rapidassist.ai.analytics.domain.model.MetricDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event publisher for analytics events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.analytics.report.generated:analytics.report.generated}")
    private String reportGeneratedTopic;

    @Value("${kafka.topic.analytics.report.requested:analytics.report.requested}")
    private String reportRequestedTopic;

    @Value("${kafka.topic.analytics.metric.created:analytics.metric.created}")
    private String metricCreatedTopic;

    @Value("${kafka.topic.analytics.dashboard.created:analytics.dashboard.created}")
    private String dashboardCreatedTopic;

    @Value("${kafka.topic.analytics.dashboard.updated:analytics.dashboard.updated}")
    private String dashboardUpdatedTopic;

    /**
     * Publish report requested event
     */
    public void publishReportRequestedEvent(AnalyticsReport report) {
        try {
            com.gogidix.rapidassist.ai.analytics.domain.event.ReportRequestedEvent event =
                    com.gogidix.rapidassist.ai.analytics.domain.event.ReportRequestedEvent.builder()
                            .eventId(java.util.UUID.randomUUID())
                            .reportId(report.getId())
                            .tenantId(report.getTenantId())
                            .reportName(report.getName())
                            .reportType(report.getReportType().name())
                            .occurredAt(java.time.LocalDateTime.now())
                            .requestedBy(report.getCreatedBy())
                            .scheduledFor(report.getScheduledFor())
                            .build();

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(reportRequestedTopic, report.getId().toString(), message);
            log.info("Published report requested event for report: {}", report.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to publish report requested event", e);
        }
    }

    /**
     * Publish report generated event
     */
    public void publishReportGeneratedEvent(AnalyticsReport report) {
        try {
            com.gogidix.rapidassist.ai.analytics.domain.event.ReportGeneratedEvent event =
                    com.gogidix.rapidassist.ai.analytics.domain.event.ReportGeneratedEvent.builder()
                            .eventId(java.util.UUID.randomUUID())
                            .reportId(report.getId())
                            .tenantId(report.getTenantId())
                            .reportName(report.getName())
                            .reportType(report.getReportType().name())
                            .occurredAt(java.time.LocalDateTime.now())
                            .triggeredBy(report.getCreatedBy())
                            .executionTimeMs(report.getExecutionTimeMs())
                            .recordCount(report.getRecordCount())
                            .status(report.getStatus().name())
                            .build();

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(reportGeneratedTopic, report.getId().toString(), message);
            log.info("Published report generated event for report: {}", report.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to publish report generated event", e);
        }
    }

    /**
     * Publish metric created event
     */
    public void publishMetricCreatedEvent(MetricDefinition metric) {
        try {
            com.gogidix.rapidassist.ai.analytics.domain.event.MetricCreatedEvent event =
                    com.gogidix.rapidassist.ai.analytics.domain.event.MetricCreatedEvent.builder()
                            .eventId(java.util.UUID.randomUUID())
                            .metricId(metric.getId())
                            .tenantId(metric.getTenantId())
                            .metricName(metric.getName())
                            .metricCode(metric.getCode())
                            .metricType(metric.getMetricType().name())
                            .occurredAt(java.time.LocalDateTime.now())
                            .createdBy(metric.getCreatedBy())
                            .build();

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(metricCreatedTopic, metric.getId().toString(), message);
            log.info("Published metric created event for metric: {}", metric.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to publish metric created event", e);
        }
    }

    /**
     * Publish dashboard created event
     */
    public void publishDashboardCreatedEvent(Dashboard dashboard) {
        try {
            com.gogidix.rapidassist.ai.analytics.domain.event.DashboardCreatedEvent event =
                    com.gogidix.rapidassist.ai.analytics.domain.event.DashboardCreatedEvent.builder()
                            .eventId(java.util.UUID.randomUUID())
                            .dashboardId(dashboard.getId())
                            .tenantId(dashboard.getTenantId())
                            .dashboardName(dashboard.getName())
                            .occurredAt(java.time.LocalDateTime.now())
                            .createdBy(dashboard.getCreatedBy())
                            .isPublic(dashboard.getIsPublic())
                            .build();

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(dashboardCreatedTopic, dashboard.getId().toString(), message);
            log.info("Published dashboard created event for dashboard: {}", dashboard.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to publish dashboard created event", e);
        }
    }

    /**
     * Publish dashboard updated event
     */
    public void publishDashboardUpdatedEvent(Dashboard dashboard) {
        try {
            com.gogidix.rapidassist.ai.analytics.domain.event.DashboardUpdatedEvent event =
                    com.gogidix.rapidassist.ai.analytics.domain.event.DashboardUpdatedEvent.builder()
                            .eventId(java.util.UUID.randomUUID())
                            .dashboardId(dashboard.getId())
                            .tenantId(dashboard.getTenantId())
                            .dashboardName(dashboard.getName())
                            .occurredAt(java.time.LocalDateTime.now())
                            .updatedBy(dashboard.getUpdatedBy())
                            .isPublic(dashboard.getIsPublic())
                            .build();

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(dashboardUpdatedTopic, dashboard.getId().toString(), message);
            log.info("Published dashboard updated event for dashboard: {}", dashboard.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to publish dashboard updated event", e);
        }
    }
}
