package com.gogidix.rapidassist.ai.speech.recognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for AudioMetadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioMetadataDto {

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
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
