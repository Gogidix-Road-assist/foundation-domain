package com.gogidix.rapidassist.ai.chatbot.application.mapper;

import com.gogidix.rapidassist.ai.chatbot.application.dto.ChatMessageDto;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ChatbotContextDto;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ChatbotSessionDto;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ConversationFlowDto;
import com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatMessage;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotContext;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ConversationFlow;
import com.gogidix.rapidassist.ai.chatbot.domain.policy.ChatbotSessionPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * MapStruct mapper for converting between domain models and DTOs.
 */
public interface ChatbotSessionMapper {

    @Mapping(target = "direction", source = "direction", qualifiedByName = "directionToString")
    ChatMessageDto toDto(ChatMessage message);

    @Mapping(target = "direction", ignore = true)
    ChatMessage toDomain(ChatMessageDto dto);

    List<ChatMessageDto> toMessageDtoList(List<ChatMessage> messages);

    ChatbotContextDto toDto(ChatbotContext context);

    @Mapping(target = "expired", expression = "java(context.isExpired())")
    @Mapping(target = "ageInSeconds", expression = "java(context.getAgeInSeconds())")
    ChatbotContextDto toContextDtoWithStatus(ChatbotContext context);

    @Mapping(target = "expired", ignore = true)
    @Mapping(target = "ageInSeconds", ignore = true)
    ChatbotContext toContextDomain(ChatbotContextDto dto);

    List<ChatbotContextDto> toContextDtoList(List<ChatbotContext> contexts);

    ConversationFlowDto toDto(ConversationFlow flow);

    @Mapping(target = "timeInCurrentStateSeconds", expression = "java(flow.getTimeInCurrentStateSeconds())")
    ConversationFlowDto toFlowDtoWithTime(ConversationFlow flow);

    @Mapping(target = "timeInCurrentStateSeconds", ignore = true)
    ConversationFlow toFlowDomain(ConversationFlowDto dto);

    List<ConversationFlowDto> toFlowDtoList(List<ConversationFlow> flows);

    @Mapping(target = "messageCount", expression = "java(session.getMessageCount())")
    @Mapping(target = "durationSeconds", expression = "java(session.getSessionDurationSeconds())")
    @Mapping(target = "currentFlowState", expression = "java(session.getCurrentFlowState())")
    @Mapping(target = "healthScore", expression = "java(ChatbotSessionPolicy.calculateHealthScore(session))")
    @Mapping(target = "recommendedAction", expression = "java(ChatbotSessionPolicy.getRecommendedAction(session))")
    @Mapping(target = "messages", source = "messages", qualifiedByName = "mapMessages")
    @Mapping(target = "contexts", source = "contexts", qualifiedByName = "mapContexts")
    @Mapping(target = "flows", source = "flows", qualifiedByName = "mapFlows")
    ChatbotSessionDto toDto(ChatbotSession session);

    @Named("mapMessages")
    default List<ChatMessageDto> mapMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(this::toDto)
                .toList();
    }

    @Named("mapContexts")
    default List<ChatbotContextDto> mapContexts(List<ChatbotContext> contexts) {
        return contexts.stream()
                .map(this::toContextDtoWithStatus)
                .toList();
    }

    @Named("mapFlows")
    default List<ConversationFlowDto> mapFlows(List<ConversationFlow> flows) {
        return flows.stream()
                .map(this::toFlowDtoWithTime)
                .toList();
    }

    @Named("directionToString")
    default String directionToString(com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection direction) {
        return direction != null ? direction.name() : null;
    }
}
