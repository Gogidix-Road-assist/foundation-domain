package com.gogidix.rapidassist.ai.computervision.domain.event;

import com.gogidix.rapidassist.ai.computervision.domain.model.AnalysisStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when image analysis is completed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisCompletedEvent {

    private UUID event_id;
    private UUID analysisId;
    private String tenantId;
    private String userId;
    private String imageUrl;
    private AnalysisStatus status;
    private Double confidenceScore;
    private Long processingTimeMs;
    private Integer detectedObjectsCount;
    private Integer detectedFacesCount;
    private Integer textRegionsCount;
    private Integer classificationCount;
    private LocalDateTime occurredAt;
    private String eventType;

    public static AnalysisCompletedEvent fromAnalysis(UUID analysisId, String tenantId, String userId,
                                                       String imageUrl, Double confidenceScore,
                                                       Long processingTimeMs, Integer detectedObjectsCount,
                                                       Integer detectedFacesCount, Integer textRegionsCount,
                                                       Integer classificationCount) {
        return AnalysisCompletedEvent.builder()
                .event_id(UUID.randomUUID())
                .analysisId(analysisId)
                .tenantId(tenantId)
                .userId(userId)
                .imageUrl(imageUrl)
                .status(AnalysisStatus.COMPLETED)
                .confidenceScore(confidenceScore)
                .processingTimeMs(processingTimeMs)
                .detectedObjectsCount(detectedObjectsCount)
                .detectedFacesCount(detectedFacesCount)
                .textRegionsCount(textRegionsCount)
                .classificationCount(classificationCount)
                .occurredAt(LocalDateTime.now())
                .eventType("AnalysisCompleted")
                .build();
    }
}
