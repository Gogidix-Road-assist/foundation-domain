package com.gogidix.rapidassist.ai.chatbot.application.command;

import com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Command to update session status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSessionStatusCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    @NotNull(message = "Status is required")
    private SessionStatus status;

    private String reason;
}
