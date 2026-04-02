package com.gogidix.rapidassist.ai.translation.application.command;

import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
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

    private String tenantId;
    private UUID sessionId;
    private TranslationSession.SessionStatus status;
    private String reason;
}
