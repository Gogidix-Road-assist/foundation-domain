package com.gogidix.rapidassist.ai.chatbot.domain.aggregate;

import com.gogidix.rapidassist.ai.chatbot.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Chatbot Session.
 * Manages the lifecycle and business logic of a chatbot conversation session.
 * Contains: ChatMessage, ChatbotContext, and ConversationFlow as child entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotSession {

    private UUID id;
    private String tenantId;
    private String userId;
    private String sessionId;
    private SessionStatus status;
    private String channel;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastActivityAt;
    private String createdBy;
    private String updatedBy;

    // Child entities (part of aggregate)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();
    
    @Builder.Default
    private List<ChatbotContext> contexts = new ArrayList<>();
    
    @Builder.Default
    private List<ConversationFlow> flows = new ArrayList<>();

    /**
     * Business logic: Initialize a new session
     */
    public static ChatbotSession initialize(String tenantId, String userId, String channel) {
        return ChatbotSession.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .sessionId(UUID.randomUUID().toString())
                .status(SessionStatus.INITIALIZED)
                .channel(channel)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .messages(new ArrayList<>())
                .contexts(new ArrayList<>())
                .flows(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Activate session
     */
    public void activate() {
        if (this.status == SessionStatus.INITIALIZED || 
            this.status == SessionStatus.PAUSED) {
            this.status = SessionStatus.ACTIVE;
            this.updatedAt = LocalDateTime.now();
            this.lastActivityAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot activate session in status: " + this.status);
        }
    }

    /**
     * Business logic: Pause session
     */
    public void pause() {
        if (this.status == SessionStatus.ACTIVE) {
            this.status = SessionStatus.PAUSED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot pause session in status: " + this.status);
        }
    }

    /**
     * Business logic: Complete session
     */
    public void complete() {
        if (this.status == SessionStatus.ACTIVE || this.status == SessionStatus.PAUSED) {
            this.status = SessionStatus.COMPLETED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot complete session in status: " + this.status);
        }
    }

    /**
     * Business logic: Terminate session
     */
    public void terminate() {
        this.status = SessionStatus.TERMINATED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if session is active
     */
    public boolean isActive() {
        return SessionStatus.ACTIVE.equals(this.status);
    }

    /**
     * Business logic: Check if session is initialized
     */
    public boolean isInitialized() {
        return SessionStatus.INITIALIZED.equals(this.status);
    }

    /**
     * Business logic: Check if session is completed
     */
    public boolean isCompleted() {
        return SessionStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if session is terminated
     */
    public boolean isTerminated() {
        return SessionStatus.TERMINATED.equals(this.status);
    }

    /**
     * Business logic: Check if session can accept new messages
     */
    public boolean canAcceptMessages() {
        return this.status == SessionStatus.ACTIVE || this.status == SessionStatus.INITIALIZED;
    }

    /**
     * Business logic: Add a message to the session
     */
    public void addMessage(ChatMessage message) {
        if (!canAcceptMessages()) {
            throw new IllegalStateException("Cannot add message to session in status: " + this.status);
        }
        message.setChatbotSessionId(this.id);
        message.setTenantId(this.tenantId);
        message.setSequenceNumber(this.messages.size() + 1);
        this.messages.add(message);
        this.lastActivityAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add context to the session
     */
    public void addContext(ChatbotContext context) {
        context.setChatbotSessionId(this.id);
        context.setTenantId(this.tenantId);
        this.contexts.add(context);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add flow to the session
     */
    public void addFlow(ConversationFlow flow) {
        flow.setChatbotSessionId(this.id);
        flow.setTenantId(this.tenantId);
        this.flows.add(flow);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get all inbound messages
     */
    public List<ChatMessage> getInboundMessages() {
        return this.messages.stream()
                .filter(ChatMessage::isInbound)
                .toList();
    }

    /**
     * Business logic: Get all outbound messages
     */
    public List<ChatMessage> getOutboundMessages() {
        return this.messages.stream()
                .filter(ChatMessage::isOutbound)
                .toList();
    }

    /**
     * Business logic: Get message count
     */
    public int getMessageCount() {
        return this.messages.size();
    }

    /**
     * Business logic: Get session duration in seconds
     */
    public long getSessionDurationSeconds() {
        if (createdAt == null) {
            return 0;
        }
        LocalDateTime endTime = this.updatedAt != null ? this.updatedAt : LocalDateTime.now();
        return java.time.Duration.between(createdAt, endTime).getSeconds();
    }

    /**
     * Business logic: Get time since last activity in seconds
     */
    public long getTimeSinceLastActivitySeconds() {
        if (lastActivityAt == null) {
            return 0;
        }
        return java.time.Duration.between(lastActivityAt, LocalDateTime.now()).getSeconds();
    }

    /**
     * Business logic: Check if session is idle (no activity for specified minutes)
     */
    public boolean isIdle(int idleThresholdMinutes) {
        long idleSeconds = getTimeSinceLastActivitySeconds();
        return idleSeconds > (idleThresholdMinutes * 60L);
    }

    /**
     * Business logic: Get current flow state
     */
    public String getCurrentFlowState() {
        return this.flows.isEmpty() ? null : 
               this.flows.get(this.flows.size() - 1).getCurrentState();
    }

    /**
     * Business logic: Update session metadata
     */
    public void updateMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
}
