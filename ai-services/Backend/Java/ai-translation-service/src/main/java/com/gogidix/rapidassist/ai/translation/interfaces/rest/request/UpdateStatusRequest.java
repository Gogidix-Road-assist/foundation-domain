package com.gogidix.rapidassist.ai.translation.interfaces.rest.request;

import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to update session status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private TranslationSession.SessionStatus status;

    private String reason;
}
