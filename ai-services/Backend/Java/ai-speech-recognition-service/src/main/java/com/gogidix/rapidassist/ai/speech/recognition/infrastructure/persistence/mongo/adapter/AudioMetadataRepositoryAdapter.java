package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.adapter;

import com.gogidix.rapidassist.ai.speech.recognition.application.port.out.AudioMetadataRepositoryPort;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.AudioMetadata;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.AudioMetadataEntity;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.repository.SpringDataAudioMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB adapter implementation for AudioMetadata Repository Port.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AudioMetadataRepositoryAdapter implements AudioMetadataRepositoryPort {

    private final SpringDataAudioMetadataRepository springDataRepository;

    @Override
    public AudioMetadata save(AudioMetadata entity) {
        log.debug("Saving audio metadata: {}", entity.getId());
        AudioMetadataEntity entityToSave = toEntity(entity);
        AudioMetadataEntity savedEntity = springDataRepository.save(entityToSave);
        return toModel(savedEntity);
    }

    @Override
    public Optional<AudioMetadata> findById(UUID id) {
        log.debug("Finding audio metadata by ID: {}", id);
        return springDataRepository.findByUuid(id)
                .map(this::toModel);
    }

    @Override
    public Optional<AudioMetadata> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding audio metadata by ID: {} and tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toModel);
    }

    @Override
    public Optional<AudioMetadata> findBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId) {
        log.debug("Finding audio metadata by speech recognition ID: {} and tenant: {}", speechRecognitionId, tenantId);
        return springDataRepository.findBySpeechRecognitionIdAndTenantId(speechRecognitionId, tenantId)
                .map(this::toModel);
    }

    @Override
    public List<AudioMetadata> findByTenantId(String tenantId) {
        log.debug("Finding all audio metadata for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting audio metadata by ID: {}", id);
        springDataRepository.deleteByUuid(id);
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Deleting audio metadata by ID: {} and tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void deleteBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId) {
        log.debug("Deleting audio metadata by speech recognition ID: {} and tenant: {}", speechRecognitionId, tenantId);
        springDataRepository.deleteBySpeechRecognitionIdAndTenantId(speechRecognitionId, tenantId);
    }

    private AudioMetadata toModel(AudioMetadataEntity entity) {
        return AudioMetadata.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .speechRecognitionId(entity.getSpeechRecognitionId())
                .fileName(entity.getFileName())
                .fileFormat(entity.getFileFormat())
                .mimeType(entity.getMimeType())
                .fileSizeBytes(entity.getFileSizeBytes())
                .durationMs(entity.getDurationMs())
                .sampleRate(entity.getSampleRate())
                .channels(entity.getChannels())
                .bitsPerSample(entity.getBitsPerSample())
                .codec(entity.getCodec())
                .bitRate(entity.getBitRate())
                .signalToNoiseRatio(entity.getSignalToNoiseRatio())
                .volumeLevel(entity.getVolumeLevel())
                .qualityRating(entity.getQualityRating())
                .storagePath(entity.getStoragePath())
                .storageType(entity.getStorageType())
                .checksum(entity.getChecksum())
                .isProcessed(entity.getIsProcessed())
                .requiresNormalization(entity.getRequiresNormalization())
                .hasBackgroundNoise(entity.getHasBackgroundNoise())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private AudioMetadataEntity toEntity(AudioMetadata model) {
        return AudioMetadataEntity.builder()
                .uuid(model.getId())
                .tenantId(model.getTenantId())
                .speechRecognitionId(model.getSpeechRecognitionId())
                .fileName(model.getFileName())
                .fileFormat(model.getFileFormat())
                .mimeType(model.getMimeType())
                .fileSizeBytes(model.getFileSizeBytes())
                .durationMs(model.getDurationMs())
                .sampleRate(model.getSampleRate())
                .channels(model.getChannels())
                .bitsPerSample(model.getBitsPerSample())
                .codec(model.getCodec())
                .bitRate(model.getBitRate())
                .signalToNoiseRatio(model.getSignalToNoiseRatio())
                .volumeLevel(model.getVolumeLevel())
                .qualityRating(model.getQualityRating())
                .storagePath(model.getStoragePath())
                .storageType(model.getStorageType())
                .checksum(model.getChecksum())
                .isProcessed(model.getIsProcessed())
                .requiresNormalization(model.getRequiresNormalization())
                .hasBackgroundNoise(model.getHasBackgroundNoise())
                .metadata(model.getMetadata())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
