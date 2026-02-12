package com.gogidix.rapidassist.ai.computervision.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.ai.computervision.domain.model.AnalysisStatus;
import com.gogidix.rapidassist.ai.computervision.domain.model.ImageFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for ImageAnalysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisDto {

    private UUID id;
    private String tenantId;
    private String userId;
    private String imageUrl;
    private String imageStoragePath;
    private ImageFormat format;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private AnalysisStatus status;
    private String analysisType;
    private Double confidenceScore;
    private String errorMessage;
    private Map<String, Object> metadata;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    private String createdBy;
    private String updatedBy;
    private Long processingTimeMs;
    private String resolution;
    private Double aspectRatio;
    private Boolean landscape;
    private Boolean portrait;
}
