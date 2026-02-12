package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.computervision.domain.model.AnalysisStatus;
import com.gogidix.rapidassist.ai.computervision.domain.model.ImageFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for ImageAnalysis
 * Maps to image_analysis collection with multi-tenancy support
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "image_analysis")
public class ImageAnalysisEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private String imageUrl;

    private String imageStoragePath;

    private ImageFormat format;

    private Long fileSize;

    private Integer width;

    private Integer height;

    @Indexed
    private AnalysisStatus status;

    @Indexed
    private String analysisType;

    private Double overallConfidence;

    private String errorMessage;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    private String createdBy;

    private String updatedBy;

    private Long processingTimeMs;

    @Indexed
    private Long version;
}
