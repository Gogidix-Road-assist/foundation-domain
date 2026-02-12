package com.gogidix.rapidassist.ai.moderation.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to moderate content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerateContentCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Content ID is required")
    private String contentId;

    @NotBlank(message = "Content is required")
    private String content;

    private String contentType;

    private String userId;

    private String userName;

    private Map<String, Object> context;

    private Map<String, Object> metadata;
}
