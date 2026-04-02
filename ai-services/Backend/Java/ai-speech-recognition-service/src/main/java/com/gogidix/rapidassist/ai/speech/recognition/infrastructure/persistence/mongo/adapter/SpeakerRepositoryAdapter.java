package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.adapter;

import com.gogidix.rapidassist.ai.speech.recognition.application.port.out.SpeakerRepositoryPort;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.Speaker;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity.SpeakerEntity;
import com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.repository.SpringDataSpeakerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB adapter implementation for Speaker Repository Port.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpeakerRepositoryAdapter implements SpeakerRepositoryPort {

    private final SpringDataSpeakerRepository springDataRepository;

    @Override
    public Speaker save(Speaker entity) {
        log.debug("Saving speaker: {}", entity.getId());
        SpeakerEntity entityToSave = toEntity(entity);
        SpeakerEntity savedEntity = springDataRepository.save(entityToSave);
        return toModel(savedEntity);
    }

    @Override
    public Optional<Speaker> findById(UUID id) {
        log.debug("Finding speaker by ID: {}", id);
        return springDataRepository.findByUuid(id)
                .map(this::toModel);
    }

    @Override
    public Optional<Speaker> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding speaker by ID: {} and tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toModel);
    }

    @Override
    public List<Speaker> findBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId) {
        log.debug("Finding speakers by speech recognition ID: {} and tenant: {}", speechRecognitionId, tenantId);
        return springDataRepository.findBySpeechRecognitionIdAndTenantId(speechRecognitionId, tenantId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Speaker> findByTenantId(String tenantId) {
        log.debug("Finding all speakers for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting speaker by ID: {}", id);
        springDataRepository.deleteByUuid(id);
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Deleting speaker by ID: {} and tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void deleteBySpeechRecognitionIdAndTenantId(UUID speechRecognitionId, String tenantId) {
        log.debug("Deleting speakers by speech recognition ID: {} and tenant: {}", speechRecognitionId, tenantId);
        springDataRepository.deleteBySpeechRecognitionIdAndTenantId(speechRecognitionId, tenantId);
    }

    private Speaker toModel(SpeakerEntity entity) {
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

    private SpeakerEntity toEntity(Speaker model) {
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
