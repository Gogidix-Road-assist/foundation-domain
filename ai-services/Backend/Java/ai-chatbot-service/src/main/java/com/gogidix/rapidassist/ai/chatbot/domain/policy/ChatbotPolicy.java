package com.gogidix.rapidassist.ai.chatbot.domain.policy;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotRequest;
import lombok.extern.slf4j.Slf4j;

public class ChatbotPolicy {
    public boolean isValid(ChatbotRequest entity) {
        if (entity == null) return false;
        if (entity.getTenantId() == null || entity.getTenantId().isEmpty()) return false;
        return true;
    }
}
