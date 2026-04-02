package com.gogidix.rapidassist.ai.translation.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to translate text.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslateTextCommand {

    private String tenantId;
    private UUID sessionId;
    private String sourceText;
    private String sourceLanguage;
    private String targetLanguage;
    private Map<String, Object> metadata;
}
