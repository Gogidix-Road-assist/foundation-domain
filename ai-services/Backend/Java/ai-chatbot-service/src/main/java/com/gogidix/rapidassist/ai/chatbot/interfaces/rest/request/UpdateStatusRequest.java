package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request;

import com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST Request to update session status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private SessionStatus status;

    private String reason;
}
