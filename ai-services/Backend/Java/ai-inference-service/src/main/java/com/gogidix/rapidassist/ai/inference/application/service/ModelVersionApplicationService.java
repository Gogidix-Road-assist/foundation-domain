package com.gogidix.rapidassist.ai.inference.application.service;

import com.gogidix.rapidassist.ai.inference.application.command.CreateModelVersionCommand;
import com.gogidix.rapidassist.ai.inference.application.command.DeployModelVersionCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.ModelVersionDto;
import com.gogidix.rapidassist.ai.inference.application.mapper.ModelVersionMapper;
import com.gogidix.rapidassist.ai.inference.application.port.in.ModelVersionUseCase;
import com.gogidix.rapidassist.ai.inference.application.port.out.ModelVersionRepositoryPort;
import com.gogidix.rapidassist.ai.inference.domain.event.ModelVersionDeployedEvent;
import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersion;
import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersionStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for Model Version use cases
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelVersionApplicationService implements ModelVersionUseCase {

    private final ModelVersionRepositoryPort modelVersionRepository;
    private final ModelVersionMapper modelVersionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ModelVersionDto createModelVersion(CreateModelVersionCommand command) {
        log.info("Creating model version: {} - {}", command.getModelId(), command.getVersion());

        if (modelVersionRepository.existsByModelIdAndVersion(command.getModelId(), command.getVersion())) {
            throw new IllegalArgumentException("Model version already exists");
        }

        ModelVersion modelVersion = ModelVersion.create(
                command.getTenantId(),
                command.getModelId(),
                command.getVersion(),
                command.getModelPath(),
                command.getModelFormat(),
                command.getCreatedBy()
        );

        if (command.getModelConfig() != null) {
            command.getModelConfig().forEach(modelVersion::addModelConfig);
        }

        if (command.getDescription() != null) {
            modelVersion.setDescription(command.getDescription());
        }

        if (command.getTags() != null) {
            modelVersion.setTags(command.getTags());
        }

        if (command.getIsDefault() != null && command.getIsDefault()) {
            // Unset other default versions
            modelVersionRepository.findByModelId(command.getModelId()).stream()
                    .filter(v -> v.getIsDefault())
                    .forEach(v -> {
                        v.setIsDefault(false);
                        modelVersionRepository.save(v);
                    });
            modelVersion.setIsDefault(true);
        }

        ModelVersion savedVersion = modelVersionRepository.save(modelVersion);
        log.info("Model version created: {}", savedVersion.getId());
        return modelVersionMapper.toDto(savedVersion);
    }

    @Override
    public ModelVersionDto getModelVersionById(UUID id) {
        log.debug("Getting model version by ID: {}", id);
        return modelVersionRepository.findById(id)
                .map(modelVersionMapper::toDto)
                .orElse(null);
    }

    @Override
    public ModelVersionDto getModelVersion(String modelId, String version) {
        log.debug("Getting model version: {} - {}", modelId, version);
        return modelVersionRepository.findByModelIdAndVersion(modelId, version)
                .map(modelVersionMapper::toDto)
                .orElse(null);
    }

    @Override
    public List<ModelVersionDto> getModelVersions(String modelId) {
        log.debug("Getting versions for model: {}", modelId);
        return modelVersionRepository.findByModelId(modelId).stream()
                .map(modelVersionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelVersionDto> getModelVersionsByTenant(String tenantId) {
        log.debug("Getting model versions for tenant: {}", tenantId);
        return modelVersionRepository.findByTenantId(tenantId).stream()
                .map(modelVersionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ModelVersionDto getDefaultModelVersion(String modelId) {
        log.debug("Getting default version for model: {}", modelId);
        return modelVersionRepository.findDefaultVersionByModelId(modelId)
                .map(modelVersionMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public ModelVersionDto deployModelVersion(DeployModelVersionCommand command) {
        log.info("Deploying model version: {} - {}", command.getModelId(), command.getVersion());

        ModelVersion modelVersion = modelVersionRepository.findByModelIdAndVersion(
                        command.getModelId(), command.getVersion())
                .orElseThrow(() -> new IllegalArgumentException("Model version not found"));

        if (!modelVersion.getTenantId().equals(command.getTenantId())) {
            throw new IllegalArgumentException("Tenant mismatch");
        }

        // Deactivate other active versions
        modelVersionRepository.findByModelId(command.getModelId()).stream()
                .filter(v -> v.isActive())
                .forEach(v -> {
                    v.deactivate();
                    modelVersionRepository.save(v);
                });

        modelVersion.activate();
        ModelVersion savedVersion = modelVersionRepository.save(modelVersion);

        // Publish domain event
        ModelVersionDeployedEvent event = ModelVersionDeployedEvent.builder()
                .eventId(UUID.randomUUID())
                .modelVersionId(savedVersion.getId())
                .tenantId(savedVersion.getTenantId())
                .modelId(savedVersion.getModelId())
                .version(savedVersion.getVersion())
                .timestamp(LocalDateTime.now())
                .deployedBy(command.getDeployedBy())
                .build();
        eventPublisher.publishEvent(event);

        log.info("Model version deployed: {}", savedVersion.getId());
        return modelVersionMapper.toDto(savedVersion);
    }

    @Override
    @Transactional
    public void retireModelVersion(UUID modelVersionId) {
        log.info("Retiring model version: {}", modelVersionId);

        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(() -> new IllegalArgumentException("Model version not found"));

        modelVersion.retire();
        modelVersionRepository.save(modelVersion);

        log.info("Model version retired: {}", modelVersionId);
    }
}
