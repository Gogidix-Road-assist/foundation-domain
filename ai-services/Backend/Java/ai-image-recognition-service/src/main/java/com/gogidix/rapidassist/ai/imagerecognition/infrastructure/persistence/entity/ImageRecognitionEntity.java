package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.imagerecognition.domain.model.RecognitionStatus;
import com.gogidix.rapidassist.ai.imagerecognition.domain.model.RecognitionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for ImageRecognition.
 * Maps to image_recognition collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "image_recognition")
public class ImageRecognitionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed(unique = true)
    private String requestId;

    @Indexed
    private String imageUrl;

    private String imageStoragePath;

    @Indexed
    private RecognitionStatus status;

    @Indexed
    private RecognitionType recognitionType;

    private String aiModelUsed;

    private Double processingTimeMs;

    private String errorMessage;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    private String createdBy;

    private String updatedBy;

    private Long version;
}
