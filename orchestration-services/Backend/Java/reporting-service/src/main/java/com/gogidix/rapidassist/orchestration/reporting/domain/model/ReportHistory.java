package com.gogidix.rapidassist.orchestration.reporting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ReportHistory entity representing audit trail of report generation.
 * Tracks all report generation attempts and outcomes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_history")
@CompoundIndex(name = "tenant_report_idx", def = "{'tenantId': 1, 'reportId': 1, 'createdAt': -1}")
public class ReportHistory {

    @Id
    private String id;

    @Indexed
    private String historyId;

    @Indexed
    private String reportId;

    @Indexed
    private String scheduleId;

    @Indexed
    private Report.ReportType reportType;

    @Indexed
    private HistoryEventType eventType;

    @Indexed
    private HistoryEventStatus eventStatus;

    private String eventDescription;

    private String errorMessage;

    private Long processingTimeMs;

    private Integer recordCount;

    private Long fileSizeBytes;

    private Map<String, Object> metadata;

    @Indexed
    private String triggeredBy;

    @Indexed
    private String tenantId;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    /**
     * Domain logic: Check if event was successful
     */
    public boolean isSuccessful() {
        return eventStatus == HistoryEventStatus.SUCCESS;
    }

    /**
     * Domain logic: Check if event was a failure
     */
    public boolean isFailed() {
        return eventStatus == HistoryEventStatus.FAILURE;
    }

    /**
     * Domain logic: Create generation started event
     */
    public static ReportHistory generationStarted(String reportId, String triggeredBy, String tenantId) {
        return ReportHistory.builder()
                .historyId(generateHistoryId())
                .reportId(reportId)
                .eventType(HistoryEventType.GENERATION_STARTED)
                .eventStatus(HistoryEventStatus.SUCCESS)
                .eventDescription("Report generation started")
                .triggeredBy(triggeredBy)
                .tenantId(tenantId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Domain logic: Create generation completed event
     */
    public static ReportHistory generationCompleted(String reportId, Long processingTimeMs, Integer recordCount, Long fileSizeBytes, String tenantId) {
        return ReportHistory.builder()
                .historyId(generateHistoryId())
                .reportId(reportId)
                .eventType(HistoryEventType.GENERATION_COMPLETED)
                .eventStatus(HistoryEventStatus.SUCCESS)
                .eventDescription("Report generation completed successfully")
                .processingTimeMs(processingTimeMs)
                .recordCount(recordCount)
                .fileSizeBytes(fileSizeBytes)
                .tenantId(tenantId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Domain logic: Create generation failed event
     */
    public static ReportHistory generationFailed(String reportId, String errorMessage, Long processingTimeMs, String tenantId) {
        return ReportHistory.builder()
                .historyId(generateHistoryId())
                .reportId(reportId)
                .eventType(HistoryEventType.GENERATION_FAILED)
                .eventStatus(HistoryEventStatus.FAILURE)
                .eventDescription("Report generation failed")
                .errorMessage(errorMessage)
                .processingTimeMs(processingTimeMs)
                .tenantId(tenantId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Domain logic: Create schedule triggered event
     */
    public static ReportHistory scheduleTriggered(String scheduleId, String reportId, String tenantId) {
        return ReportHistory.builder()
                .historyId(generateHistoryId())
                .scheduleId(scheduleId)
                .reportId(reportId)
                .eventType(HistoryEventType.SCHEDULE_TRIGGERED)
                .eventStatus(HistoryEventStatus.SUCCESS)
                .eventDescription("Schedule triggered report generation")
                .tenantId(tenantId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static String generateHistoryId() {
        return "HIS-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    public enum HistoryEventType {
        GENERATION_STARTED,
        GENERATION_COMPLETED,
        GENERATION_FAILED,
        SCHEDULE_TRIGGERED,
        TEMPLATE_UPDATED,
        REPORT_DOWNLOADED,
        REPORT_DELETED,
        REPORT_EXPIRED
    }

    public enum HistoryEventStatus {
        SUCCESS,
        FAILURE,
        PENDING
    }
}
