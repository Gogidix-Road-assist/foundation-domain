package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.adapter;

import com.gogidix.rapidassist.ai.speech.recognition.application.port.out.TranscriptionRepositoryPort;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.Transcription;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.TranscriptionEntity;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.repository.SpringDataTranscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB adapter implementation for Transcription Repository Port.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TranscriptionRepositoryAdapter implements TranscriptionRepositoryPort {

    private final SpringDataTranscriptionRepository springDataRepository;

    @Override
    public Transcription save(Transcription entity) {
        log.debug("Saving transcription: {}", entity.getId());
        TranscriptionEntity entityToSave = toEntity(entity);
        TranscriptionEntity savedEntity = springDataRepository.save(entityToSave);
        return toModel(savedEntity);
    }

    @Override
    public Optional<Transcription> findById(UUID id) {
        log.debug("Finding transcription by ID: {}", id);
        return springDataRepository.findByUuid(id)
                .map(this::toModel);
    }

    @Override
    public Optional<Transcription> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding transcription by ID: {} and tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toModel);
    }

    @Override
    public List<Transcription> findBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId) {
        log.debug("Finding transcriptions by speech recognition ID: {} and tenant: {}", speechRecognitionId, tenantId);
        return springDataRepository.findBySpeechRecognitionIdAndTenantId(speechRecognitionId, tenantId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transcription> findByTenantId(String tenantId) {
        log.debug("Finding all transcriptions for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting transcription by ID: {}", id);
        springDataRepository.deleteByUuid(id);
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Deleting transcription by ID: {} and tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void deleteBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId) {
        log.debug("Deleting transcriptions by speech recognition ID: {} and tenant: {}", speechRecognitionId, tenantId);
        springDataRepository.deleteBySpeechRecognitionIdAndTenantId(speechRecognitionId, tenantId);
    }

    private Transcription toModel(TranscriptionEntity entity) {
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

    private TranscriptionEntity toEntity(Transcription model) {
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
}
