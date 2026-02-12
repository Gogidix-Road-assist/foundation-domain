package com.gogidix.rapidassist.ai.chatbot.application.mapper;

import com.gogidix.rapidassist.ai.chatbot.application.command.CreateConversationCommand;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ConversationDto;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ConversationMapper {
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ConversationDto entityToDto(ChatbotRequest entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ChatbotRequest commandToEntity(CreateConversationCommand command);

    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    List<ConversationDto> entitiesToDtos(List<ChatbotRequest> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromCommand(CreateConversationCommand command, @MappingTarget ChatbotRequest entity);
}
