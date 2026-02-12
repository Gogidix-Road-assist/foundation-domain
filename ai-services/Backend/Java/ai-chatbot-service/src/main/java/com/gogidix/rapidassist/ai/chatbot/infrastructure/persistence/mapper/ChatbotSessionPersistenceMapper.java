
package com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.mapper;



import com.fasterxml.jackson.databind.ObjectMapper;

import com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatMessage;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotContext;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ConversationFlow;

import com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.entity.*;

import lombok.RequiredArgsConstructor;

import lombok.SneakyThrows;

import org.springframework.stereotype.Component;



/**

 * MapStruct-style mapper for converting between domain models and JPA entities.

 * Handles JSON serialization for metadata fields.

 */



@Component
@RequiredArgsConstructor
public class ChatbotSessionPersistenceMapper {

    private final ObjectMapper objectMapper;

    // ==================== ChatbotSession ====================

    @SneakyThrows
    public ChatbotSessionEntity toEntity(ChatbotSession domain) {
        return ChatbotSessionEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .userId(domain.getUserId())
                .sessionId(domain.getSessionId())
                .status(domain.getStatus())
                .channel(domain.getChannel())
                .metadata(domain.getMetadata() != null ? objectMapper.writeValueAsString(domain.getMetadata()) : null)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .lastActivityAt(domain.getLastActivityAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    @SneakyThrows
    public ChatbotSession toDomain(ChatbotSessionEntity entity) {
        return ChatbotSession.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .userId(entity.getUserId())
                .sessionId(entity.getSessionId())
                .status(entity.getStatus())
                .channel(entity.getChannel())
                .metadata(entity.getMetadata() != null ? objectMapper.readValue(entity.getMetadata(), java.util.Map.class) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastActivityAt(entity.getLastActivityAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .messages(new java.util.ArrayList<>())
                .contexts(new java.util.ArrayList<>())
                .flows(new java.util.ArrayList<>())
                .build();
    }

    // ==================== ChatMessage ====================

    @SneakyThrows
    public ChatMessageEntity toEntity(ChatMessage domain) {
        return ChatMessageEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .sessionId(domain.getChatbotSessionId())
                .direction(domain.getDirection())
                .content(domain.getContent())
                .messageType(domain.getMessageType())
                .metadata(domain.getMetadata() != null ? objectMapper.writeValueAsString(domain.getMetadata()) : null)
                .timestamp(domain.getTimestamp())
                .sequenceNumber(domain.getSequenceNumber())
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    @SneakyThrows
    public ChatMessage toDomain(ChatMessageEntity entity) {
        return ChatMessage.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .chatbotSessionId(entity.getSessionId())
                .direction(entity.getDirection())
                .content(entity.getContent())
                .messageType(entity.getMessageType())
                .metadata(entity.getMetadata() != null ? objectMapper.readValue(entity.getMetadata(), java.util.Map.class) : null)
                .timestamp(entity.getTimestamp())
                .sequenceNumber(entity.getSequenceNumber())
                .build();
    }

    // ==================== ChatbotContext ====================

    @SneakyThrows
    public ChatbotContextEntity toEntity(ChatbotContext domain) {
        return ChatbotContextEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .sessionId(domain.getChatbotSessionId())
                .contextKey(domain.getContextKey())
                .contextValue(domain.getContextValue())
                .contextType(domain.getContextType())
                .additionalContext(domain.getAdditionalContext() != null ? objectMapper.writeValueAsString(domain.getAdditionalContext()) : null)
                .ttl(domain.getTtl())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    @SneakyThrows
    public ChatbotContext toDomain(ChatbotContextEntity entity) {
        return ChatbotContext.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .chatbotSessionId(entity.getSessionId())
                .contextKey(entity.getContextKey())
                .contextValue(entity.getContextValue())
                .contextType(entity.getContextType())
                .additionalContext(entity.getAdditionalContext() != null ? objectMapper.readValue(entity.getAdditionalContext(), java.util.Map.class) : null)
                .ttl(entity.getTtl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ==================== ConversationFlow ====================

    @SneakyThrows
    public ConversationFlowEntity toEntity(ConversationFlow domain) {
        return ConversationFlowEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .sessionId(domain.getChatbotSessionId())
                .currentState(domain.getCurrentState())
                .previousState(domain.getPreviousState())
                .nextState(domain.getNextState())
                .flowType(domain.getFlowType())
                .flowParameters(domain.getFlowParameters() != null ? objectMapper.writeValueAsString(domain.getFlowParameters()) : null)
                .stepNumber(domain.getStepNumber())
                .stateEnteredAt(domain.getStateEnteredAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    @SneakyThrows
    public ConversationFlow toDomain(ConversationFlowEntity entity) {
        return ConversationFlow.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .chatbotSessionId(entity.getSessionId())
                .currentState(entity.getCurrentState())
                .previousState(entity.getPreviousState())
                .nextState(entity.getNextState())
                .flowType(entity.getFlowType())
                .flowParameters(entity.getFlowParameters() != null ? objectMapper.readValue(entity.getFlowParameters(), java.util.Map.class) : null)
                .stepNumber(entity.getStepNumber())
                .stateEnteredAt(entity.getStateEnteredAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
