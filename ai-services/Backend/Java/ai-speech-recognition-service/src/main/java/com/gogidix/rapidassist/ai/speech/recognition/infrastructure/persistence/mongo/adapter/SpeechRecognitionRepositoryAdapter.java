package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.adapter;

import com.gogidix.rapidassist.ai.speech.recognition.application.port.out.SpeechRecognitionRepositoryPort;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.SpeechRecognition;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.Transcription;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.Speaker;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.SpeechRecognitionEntity;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.TranscriptionEntity;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.SpeakerEntity;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.repository.SpringDataSpeechRecognitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB adapter implementation for Speech Recognition Repository Port.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpeechRecognitionRepositoryAdapter implements SpeechRecognitionRepositoryPort {

    private final SpringDataSpeechRecognitionRepository springDataRepository;

    @Override
    public SpeechRecognition save(SpeechRecognition entity) {
        log.debug("Saving speech recognition: {}", entity.getId());
        SpeechRecognitionEntity entityToSave = toEntity(entity);
        SpeechRecognitionEntity savedEntity = springDataRepository.save(entityToSave);
        return toModel(savedEntity);
    }

    @Override
    public Optional<SpeechRecognition> findById(UUID id) {
        log.debug("Finding speech recognition by ID: {}", id);
        return springDataRepository.findByUuid(id)
                .map(this::toModel);
    }

    @Override
    public Optional<SpeechRecognition> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding speech recognition by ID: {} and tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toModel);
    }

    @Override
    public Optional<SpeechRecognition> findByRequestIdAndTenantId(String requestId, String tenantId) {
        log.debug("Finding speech recognition by request ID: {} and tenant: {}", requestId, tenantId);
        return springDataRepository.findByRequestIdAndTenantId(requestId, tenantId)
                .map(this::toModel);
    }

    @Override
    public List<SpeechRecognition> findByTenantId(String tenantId) {
        log.debug("Finding all speech recognition for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpeechRecognition> findByUserIdAndTenantId(String userId, String tenantId) {
        log.debug("Finding speech recognition by user ID: {} and tenant: {}", userId, tenantId);
        return springDataRepository.findByUserIdAndTenantId(userId, tenantId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpeechRecognition> findByStatusAndTenantId(String status, String tenantId) {
        log.debug("Finding speech recognition by status: {} and tenant: {}", status, tenantId);
        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting speech recognition by ID: {}", id);
        springDataRepository.deleteByUuid(id);
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Deleting speech recognition by ID: {} and tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Checking if speech recognition exists by ID: {} and tenant: {}", id, tenantId);
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    // Entity to Model conversion
    private SpeechRecognition toModel(SpeechRecognitionEntity entity) {
        return SpeechRecognition.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .userId(entity.getUserId())
                .requestId(entity.getRequestId())
                .audioFilePath(entity.getAudioFilePath())
                .audioFormat(entity.getAudioFormat())
                .audioDurationMs(entity.getAudioDurationMs())
                .audioFileSizeBytes(entity.getAudioFileSizeBytes())
                .sampleRate(entity.getSampleRate())
                .channels(entity.getChannels())
                .language(entity.getLanguage())
                .model(entity.getModel())
                .enablePunctuation(entity.getEnablePunctuation())
                .enableSpeakerDiarization(entity.getEnableSpeakerDiarization())
                .enableWordTimestamps(entity.getEnableWordTimestamps())
                .maxSpeakers(entity.getMaxSpeakers())
                .transcription(entity.getTranscription())
                .confidenceScore(entity.getConfidenceScore())
                .transcriptions(entity.getTranscriptions() != null ?
                    entity.getTranscriptions().stream()
                        .map(this::toTranscriptionModel)
                        .collect(Collectors.toList()) : null)
                .speakers(entity.getSpeakers() != null ?
                    entity.getSpeakers().stream()
                        .map(this::toSpeakerModel)
                        .collect(Collectors.toList()) : null)
                .status(entity.getStatus())
                .processingStatus(entity.getProcessingStatus())
                .errorMessage(entity.getErrorMessage())
                .processingAttempts(entity.getProcessingAttempts())
                .processingStartedAt(entity.getProcessingStartedAt())
                .processingCompletedAt(entity.getProcessingCompletedAt())
                .processingDurationMs(entity.getProcessingDurationMs())
                .metadata(entity.getMetadata())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    // Model to Entity conversion
    private SpeechRecognitionEntity toEntity(SpeechRecognition model) {
        return SpeechRecognitionEntity.builder()
                .uuid(model.getId())
                .tenantId(model.getTenantId())
                .userId(model.getUserId())
                .requestId(model.getRequestId())
                .audioFilePath(model.getAudioFilePath())
                .audioFormat(model.getAudioFormat())
                .audioDurationMs(model.getAudioDurationMs())
                .audioFileSizeBytes(model.getAudioFileSizeBytes())
                .sampleRate(model.getSampleRate())
                .channels(model.getChannels())
                .language(model.getLanguage())
                .model(model.getModel())
                .enablePunctuation(model.getEnablePunctuation())
                .enableSpeakerDiarization(model.getEnableSpeakerDiarization())
                .enableWordTimestamps(model.getEnableWordTimestamps())
                .maxSpeakers(model.getMaxSpeakers())
                .transcription(model.getTranscription())
                .confidenceScore(model.getConfidenceScore())
                .transcriptions(model.getTranscriptions() != null ?
                    model.getTranscriptions().stream()
                        .map(this::toTranscriptionEntity)
                        .collect(Collectors.toList()) : null)
                .speakers(model.getSpeakers() != null ?
                    model.getSpeakers().stream()
                        .map(this::toSpeakerEntity)
                        .collect(Collectors.toList()) : null)
                .status(model.getStatus())
                .processingStatus(model.getProcessingStatus())
                .errorMessage(model.getErrorMessage())
                .processingAttempts(model.getProcessingAttempts())
                .processingStartedAt(model.getProcessingStartedAt())
                .processingCompletedAt(model.getProcessingCompletedAt())
                .processingDurationMs(model.getProcessingDurationMs())
                .metadata(model.getMetadata())
                .version(model.getVersion())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .createdBy(model.getCreatedBy())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    private Transcription toTranscriptionModel(TranscriptionEntity entity) {
        return Transcription.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .speechRecognitionId(entity.getSpeechRecognitionId())
                .text(entity.getText())
                .segmentIndex(entity.getSegmentIndex())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .confidence(entity.getConfidence())
                .speakerId(entity.getSpeakerId())
                .speakerLabel(entity.getSpeakerLabel())
                .speakerConfidence(entity.getSpeakerConfidence())
                .detectedLanguage(entity.getDetectedLanguage())
                .languageConfidence(entity.getLanguageConfidence())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private TranscriptionEntity toTranscriptionEntity(Transcription model) {
        return TranscriptionEntity.builder()
                .uuid(model.getId())
                .tenantId(model.getTenantId())
                .speechRecognitionId(model.getSpeechRecognitionId())
                .text(model.getText())
                .segmentIndex(model.getSegmentIndex())
                .startTime(model.getStartTime())
                .endTime(model.getEndTime())
                .confidence(model.getConfidence())
                .speakerId(model.getSpeakerId())
                .speakerLabel(model.getSpeakerLabel())
                .speakerConfidence(model.getSpeakerConfidence())
                .detectedLanguage(model.getDetectedLanguage())
                .languageConfidence(model.getLanguageConfidence())
                .metadata(model.getMetadata())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    private Speaker toSpeakerModel(SpeakerEntity entity) {
        return Speaker.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .speechRecognitionId(entity.getSpeechRecognitionId())
                .speakerId(entity.getSpeakerId())
                .speakerLabel(entity.getSpeakerLabel())
                .confidence(entity.getConfidence())
                .gender(entity.getGender())
                .genderConfidence(entity.getGenderConfidence())
                .ageGroup(entity.getAgeGroup())
                .ageGroupConfidence(entity.getAgeGroupConfidence())
                .segmentCount(entity.getSegmentCount())
                .totalSpeakingTime(entity.getTotalSpeakingTime())
                .averageSpeakingTime(entity.getAverageSpeakingTime())
                .firstStartTime(entity.getFirstStartTime())
                .lastEndTime(entity.getLastEndTime())
                .speakerEmbedding(entity.getSpeakerEmbedding())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private SpeakerEntity toSpeakerEntity(Speaker model) {
        return SpeakerEntity.builder()
                .uuid(model.getId())
                .tenantId(model.getTenantId())
                .speechRecognitionId(model.getSpeechRecognitionId())
                .speakerId(model.getSpeakerId())
                .speakerLabel(model.getSpeakerLabel())
                .confidence(model.getConfidence())
                .gender(model.getGender())
                .genderConfidence(model.getGenderConfidence())
                .ageGroup(model.getAgeGroup())
                .ageGroupConfidence(model.getAgeGroupConfidence())
                .segmentCount(model.getSegmentCount())
                .totalSpeakingTime(model.getTotalSpeakingTime())
                .averageSpeakingTime(model.getAverageSpeakingTime())
                .firstStartTime(model.getFirstStartTime())
                .lastEndTime(model.getLastEndTime())
                .speakerEmbedding(model.getSpeakerEmbedding())
                .metadata(model.getMetadata())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
