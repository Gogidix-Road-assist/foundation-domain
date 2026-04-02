package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ABTest;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ABTestStatus;
import com.gogidix.rapidassist.ai.modelmanagement.domain.repository.ABTestRepositoryPort;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ABTestEntity;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository.SpringDataABTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter implementing ABTestRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class ABTestRepositoryAdapter implements ABTestRepositoryPort {

    private final SpringDataABTestRepository springDataRepository;

    @Override
    public ABTest save(String tenantId, ABTest abTest) {
        ABTestEntity entity = toEntity(abTest);
        entity.setTenantId(tenantId);
        ABTestEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<ABTest> findById(String tenantId, UUID id) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<ABTest> findAll(String tenantId) {
        return springDataRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ABTest> findByStatus(String tenantId, ABTestStatus status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ABTest> findByControlModelVersionId(String tenantId, UUID controlModelVersionId) {
        return springDataRepository.findByTenantIdAndControlModelVersionId(tenantId, controlModelVersionId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ABTest> findByTreatmentModelVersionId(String tenantId, UUID treatmentModelVersionId) {
        return springDataRepository.findByTenantIdAndTreatmentModelVersionId(tenantId, treatmentModelVersionId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ABTest> findByName(String tenantId, String name) {
        return springDataRepository.findByTenantIdAndName(tenantId, name)
                .map(this::toDomain);
    }

    @Override
    public List<ABTest> findRunningTests(String tenantId) {
        return springDataRepository.findByTenantIdAndStatusOrderByCreatedAtDesc(
                tenantId, ABTestStatus.RUNNING).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void delete(String tenantId, UUID id) {
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    private ABTest toDomain(ABTestEntity entity) {
        return ABTest.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .controlModelVersionId(entity.getControlModelVersionId())
                .treatmentModelVersionId(entity.getTreatmentModelVersionId())
                .trafficSplitPercentage(entity.getTrafficSplitPercentage())
                .status(entity.getStatus())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .totalParticipants(entity.getTotalParticipants())
                .controlParticipants(entity.getControlParticipants())
                .treatmentParticipants(entity.getTreatmentParticipants())
                .controlMetrics(entity.getControlMetrics())
                .treatmentMetrics(entity.getTreatmentMetrics())
                .winner(entity.getWinner())
                .statisticalSignificance(entity.getStatisticalSignificance())
                .metadata(entity.getMetadata())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    private ABTestEntity toEntity(ABTest abTest) {
        return ABTestEntity.builder()
                .uuid(abTest.getId() != null ? abTest.getId() : UUID.randomUUID())
                .tenantId(abTest.getTenantId())
                .name(abTest.getName())
                .description(abTest.getDescription())
                .controlModelVersionId(abTest.getControlModelVersionId())
                .treatmentModelVersionId(abTest.getTreatmentModelVersionId())
                .trafficSplitPercentage(abTest.getTrafficSplitPercentage())
                .status(abTest.getStatus())
                .startTime(abTest.getStartTime())
                .endTime(abTest.getEndTime())
                .totalParticipants(abTest.getTotalParticipants())
                .controlParticipants(abTest.getControlParticipants())
                .treatmentParticipants(abTest.getTreatmentParticipants())
                .controlMetrics(abTest.getControlMetrics())
                .treatmentMetrics(abTest.getTreatmentMetrics())
                .winner(abTest.getWinner())
                .statisticalSignificance(abTest.getStatisticalSignificance())
                .metadata(abTest.getMetadata())
                .createdBy(abTest.getCreatedBy())
                .createdAt(abTest.getCreatedAt())
                .updatedAt(abTest.getUpdatedAt())
                .version(abTest.getVersion())
                .build();
    }
}
