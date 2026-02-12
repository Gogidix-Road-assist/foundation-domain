package com.gogidix.rapidassist.ai.moderation.application.dto;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for ModerationResult
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationResultDto {

    private String id;
    private String tenantId;
    private String contentId;
    private String contentType;
    private String content;
    private ModerationResult.ModerationStatus status;
    private Double confidenceScore;
    private List<ModerationResult.RuleViolation> violations;
    private Map<String, Object> analysisDetails;
    private String moderatedBy;
    private LocalDateTime moderatedAt;
    private LocalDateTime createdAt;
    private String reviewNotes;
    private Map<String, Object> metadata;
}
