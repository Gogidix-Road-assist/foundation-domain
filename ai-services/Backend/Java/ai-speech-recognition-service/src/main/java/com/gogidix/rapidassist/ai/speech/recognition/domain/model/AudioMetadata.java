package com.gogidix.rapidassist.ai.speech.recognition.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing Audio Metadata.
 * Pure domain model without persistence annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioMetadata {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID speechRecognitionId;

    // Audio file information
    private String fileName;
    private String fileFormat;
    private String mimeType;
    private Long fileSizeBytes;
    private Long durationMs;

    // Audio properties
    private Integer sampleRate;
    private Integer channels;
    private Integer bitsPerSample;
    private String codec;
    private Double bitRate;

    // Quality metrics
    private Double signalToNoiseRatio;
    private Double volumeLevel;
    private String qualityRating;

    // File storage
    private String storagePath;
    private String storageType;
    private String checksum;

    // Processing flags
    private Boolean isProcessed;
    private Boolean requiresNormalization;
    private Boolean hasBackgroundNoise;

    // Metadata
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
