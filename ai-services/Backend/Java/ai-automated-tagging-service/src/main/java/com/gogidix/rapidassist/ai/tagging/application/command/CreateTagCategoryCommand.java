package com.gogidix.rapidassist.ai.tagging.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to create a new tag category
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTagCategoryCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Category name is required")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 50, message = "Icon must not exceed 50 characters")
    private String icon;

    private Integer displayOrder;

    private String createdBy;
}
