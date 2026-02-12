package com.gogidix.rapidassist.ai.chatbot.application.port.in;

import com.gogidix.rapidassist.ai.chatbot.application.command.CreateConversationCommand;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ConversationDto;

import java.util.List;
import java.util.UUID;

public interface ChatbotUseCase {
    ConversationDto create(CreateConversationCommand command);
    ConversationDto getById(UUID id);
    List<ConversationDto> getAll();
    void delete(UUID id);
}
