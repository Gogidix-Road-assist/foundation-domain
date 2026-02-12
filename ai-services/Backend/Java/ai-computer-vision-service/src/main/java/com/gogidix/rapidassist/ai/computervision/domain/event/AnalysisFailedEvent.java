package com.gogidix.rapidassist.ai.computervision.domain.event;

import com.gogidix.rapidassist.ai.computervision.domain.model.AnalysisStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when image analysis fails
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisFailedEvent {

    private UUID event_id;
    private UUID analysisId;
    private String tenantId;
    private String userId;
    private String imageUrl;
    private AnalysisStatus status;
    private String errorMessage;
    private String errorCategory;
    private LocalDateTime occurredAt;
    private String eventType;

    public static AnalysisFailedEvent fromAnalysis(UUID analysisId, String tenantId, String userId,
                                                    String imageUrl, String errorMessage, String errorCategory) {
        return AnalysisFailedEvent.builder()
                .event_id(UUID.randomUUID())
                .analysisId(analysisId)
                .tenantId(tenantId)
                .userId(userId)
                .imageUrl(imageUrl)
                .status(AnalysisStatus.FAILED)
                .errorMessage(errorMessage)
                .errorCategory(errorCategory)
                .occurredAt(LocalDateTime.now())
                .eventType("AnalysisFailed")
                .build();
    }
}
