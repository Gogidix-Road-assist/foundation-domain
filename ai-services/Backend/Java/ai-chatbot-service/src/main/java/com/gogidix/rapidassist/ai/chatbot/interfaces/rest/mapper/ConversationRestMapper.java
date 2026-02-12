package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.mapper;

import com.gogidix.rapidassist.ai.chatbot.application.command.CreateConversationCommand;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ConversationDto;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request.ConversationRequest;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.response.ConversationResponse;
import org.mapstruct.Mapper;

public interface ConversationRestMapper {
    ConversationResponse dtoToResponse(ConversationDto dto);
    CreateConversationCommand requestToCommand(ConversationRequest request);
}
