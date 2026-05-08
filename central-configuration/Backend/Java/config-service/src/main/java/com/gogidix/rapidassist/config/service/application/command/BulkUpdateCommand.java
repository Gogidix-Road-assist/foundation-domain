package com.gogidix.rapidassist.config.service.application.command;

import java.util.List;

/**
 * Command object for bulk updating multiple configurations.
 *
 * <p>This command allows atomic updates to multiple configurations
 * within a single tenant and namespace.
 */
public record BulkUpdateCommand(
    String tenantId,
    List<ConfigurationUpdateItem> updates,
    String updatedBy,
    String reason
) {
    /**
     * Represents a single configuration update in a bulk operation.
     */
    public record ConfigurationUpdateItem(
        String configKey,
        String environment,
        String namespace,
        Object value
    ) {}

    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && updates != null && !updates.isEmpty()
            && updatedBy != null && !updatedBy.isBlank()
            && updates.stream().allMatch(item ->
                item.configKey() != null && !item.configKey().isBlank()
                && item.environment() != null && !item.environment().isBlank()
                && item.namespace() != null && !item.namespace().isBlank());
    }
}
