package com.gogidix.rapidassist.ai.chatbot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing the flow of a conversation.
 * Tracks the conversation state transitions and flow control.
 * Pure domain model without JPA annotations.
 * Part of the ChatbotSession aggregate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationFlow {

    private UUID id;
    private String tenantId;
    private UUID chatbotSessionId;
    private String currentState;
    private String previousState;
    private String nextState;
    private String flowType;
    private java.util.Map<String, Object> flowParameters;
    private Integer stepNumber;
    private LocalDateTime stateEnteredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Transition to next state
     */
    public void transitionTo(String newState) {
        this.previousState = this.currentState;
        this.currentState = newState;
        this.stateEnteredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.stepNumber != null) {
            this.stepNumber++;
        } else {
            this.stepNumber = 1;
        }
    }

    /**
     * Business logic: Check if flow is in a specific state
     */
    public boolean isInState(String state) {
        return state != null && state.equals(this.currentState);
    }

    /**
     * Business logic: Get time spent in current state
     */
    public long getTimeInCurrentStateSeconds() {
        if (stateEnteredAt == null) {
            return 0;
        }
        return java.time.Duration.between(stateEnteredAt, LocalDateTime.now()).getSeconds();
    }

    /**
     * Business logic: Set next state
     */
    public void setNextState(String state) {
        this.nextState = state;
        this.updatedAt = LocalDateTime.now();
    }
}
