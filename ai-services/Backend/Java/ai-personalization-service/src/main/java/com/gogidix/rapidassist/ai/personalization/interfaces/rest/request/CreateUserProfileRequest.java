package com.gogidix.rapidassist.ai.personalization.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * REST request to create a new user profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserProfileRequest {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String segmentId;

    // Demographics
    private Integer age;
    private String gender;
    private String location;
    private String language;
    private String timezone;

    // Initial interests and preferences
    private Map<String, Object> interests;
    private Map<String, Object> preferences;

    // Attributes
    private Map<String, Object> attributes;
}
