package com.gogidix.rapidassist.ai.translation.application.command;

import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to create a translation session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTranslationSessionCommand {

    private String tenantId;
    private String userId;
    private TranslationSession.SessionType sessionType;
    private String sourceLanguage;
    private String targetLanguage;
    private String channel;
    private Map<String, Object> metadata;
}
