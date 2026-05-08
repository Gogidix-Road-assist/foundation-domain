package com.gogidix.rapidassist.config.service.adapters.in.web.dto.request;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Request DTO for creating a new configuration.
 * Contains all required and optional fields for configuration creation.
 */
@Schema(description = "Request to create a new configuration")
public record CreateConfigurationRequest(

    @Schema(description = "Tenant identifier", example = "tenant-001", required = true)
    @NotBlank(message = "Tenant ID is required")
    String tenantId,

    @Schema(description = "Configuration key", example = "api.timeout", required = true)
    @NotBlank(message = "Configuration key is required")
    String configKey,

    @Schema(description = "Environment", example = "production", required = true)
    @NotBlank(message = "Environment is required")
    String environment,

    @Schema(description = "Namespace", example = "api-gateway", required = true)
    @NotBlank(message = "Namespace is required")
    String namespace,

    @Schema(description = "Configuration value", example = "30000", required = true)
    @NotNull(message = "Configuration value is required")
    Object value,

    @Schema(description = "Data type", example = "INTEGER", required = true)
    @NotNull(message = "Data type is required")
    Configuration.ConfigurationDataType dataType,

    @Schema(description = "Whether value should be encrypted", example = "false")
    boolean encrypted,

    @Schema(description = "Whether configuration is required", example = "true")
    boolean required,

    @Schema(description = "Default value", example = "30000")
    Object defaultValue,

    @Schema(description = "Configuration description", example = "API timeout in milliseconds")
    String description,

    @Schema(description = "Tags for categorization", example = "[\"api\", \"performance\"]")
    Set<String> tags,

    @Schema(description = "Additional metadata", example = "{\"owner\":\"team-a\"}")
    Map<String, Object> metadata,

    @Schema(description = "Configuration schema for validation")
    ConfigurationSchema schema,

    @Schema(description = "Reason for creation/change", example = "Adding timeout configuration")
    String reason
) {
    /**
     * Validates that encrypted fields are not used with incompatible data types.
     */
    public CreateConfigurationRequest {
        if (encrypted && dataType == Configuration.ConfigurationDataType.OBJECT) {
            throw new IllegalArgumentException("Encrypted configurations cannot have OBJECT data type");
        }
        if (encrypted && dataType == Configuration.ConfigurationDataType.ARRAY) {
            throw new IllegalArgumentException("Encrypted configurations cannot have ARRAY data type");
        }
    }
}
