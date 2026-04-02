package com.gogidix.rapidassist.ai.tagging.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Command to create a new tag
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTagCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Tag name is required")
    @Size(min = 1, max = 100, message = "Tag name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 20, message = "Color code must not exceed 20 characters")
    private String color;

    private UUID categoryId;

    private String createdBy;
}
