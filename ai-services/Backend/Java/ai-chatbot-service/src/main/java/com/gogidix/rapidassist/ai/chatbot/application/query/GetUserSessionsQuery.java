package com.gogidix.rapidassist.ai.chatbot.application.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get all sessions for a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserSessionsQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String status;

    private Integer page;

    private Integer size;

    private String sortBy;

    private String sortDirection;
}
