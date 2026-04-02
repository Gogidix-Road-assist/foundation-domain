package com.gogidix.rapidassist.ai.inference.application.port.in;

import com.gogidix.rapidassist.ai.inference.application.command.CreateModelVersionCommand;
import com.gogidix.rapidassist.ai.inference.application.command.DeployModelVersionCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.ModelVersionDto;

import java.util.List;
import java.util.UUID;

/**
 * Input port for Model Version use cases
 */
public interface ModelVersionUseCase {

    ModelVersionDto createModelVersion(CreateModelVersionCommand command);

    ModelVersionDto getModelVersionById(UUID id);

    ModelVersionDto getModelVersion(String modelId, String version);

    List<ModelVersionDto> getModelVersions(String modelId);

    List<ModelVersionDto> getModelVersionsByTenant(String tenantId);

    ModelVersionDto getDefaultModelVersion(String modelId);

    ModelVersionDto deployModelVersion(DeployModelVersionCommand command);

    void retireModelVersion(UUID modelVersionId);
}
