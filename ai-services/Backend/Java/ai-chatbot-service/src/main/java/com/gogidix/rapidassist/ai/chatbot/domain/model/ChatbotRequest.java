package com.gogidix.rapidassist.ai.chatbot.domain.model;

import com.gogidix.rapidassist.ai.chatbot.domain.event.ChatbotRequestCompletedEvent;
import com.gogidix.rapidassist.ai.chatbot.domain.event.ChatbotRequestFailedEvent;
import com.gogidix.rapidassist.ai.chatbot.domain.event.ChatbotRequestRequestedEvent;
import com.gogidix.rapidassist.ai.chatbot.domain.exception.ChatbotException;
import lombok.*;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Chatbot Request.
 * Represents a request to interact with the AI chatbot.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chatbot_request")
public class ChatbotRequest {

    @Id
    @Builder.Default
    private String id = java.util.UUID.randomUUID().toString();

    @Indexed
    private String tenantId;

    @Indexed
    private String sessionId;

    private String userMessage;

    private String botResponse;

    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Indexed
    private ChannelType channelType;

    private IntentType intentType;

    private Double confidenceScore;

    private String conversationContext;

    private String metadata;

    @Indexed
    private String userId;

    @Builder.Default
    private String languageCode = "en";

    private String errorMessage;

    private Long processingTimeMs;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();

    private Instant completedAt;

    @Builder.Default
    private Integer priority = 5;

    private Long version;

    /**
     * Called by MongoTemplate before saving
     */
    public void onBeforeSave() {
        this.updatedAt = Instant.now();
    }

    // Business logic methods

    /**
     * Checks if the request is in PENDING status
     */
    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }

    /**
     * Checks if the request is in PROCESSING status
     */
    public boolean isProcessing() {
        return status == RequestStatus.PROCESSING;
    }

    /**
     * Checks if the request has been COMPLETED
     */
    public boolean isCompleted() {
        return status == RequestStatus.COMPLETED;
    }

    /**
     * Checks if the request has FAILED
     */
    public boolean isFailed() {
        return status == RequestStatus.FAILED;
    }

    /**
     * Marks the request as being processed
     */
    public void markAsProcessing() {
        if (status != RequestStatus.PENDING && status != RequestStatus.QUEUED) {
            throw new ChatbotException(
                "Cannot mark request as processing. Current status: " + status
            );
        }
        this.status = RequestStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    /**
     * Marks the request as completed with bot response
     */
    public void markAsCompleted(String botResponse, IntentType intentType, Double confidence,
                                String context, Long processingTime) {
        if (!isProcessing()) {
            throw new ChatbotException(
                "Cannot mark request as completed. Current status: " + status
            );
        }

        this.status = RequestStatus.COMPLETED;
        this.botResponse = botResponse;
        this.intentType = intentType;
        this.confidenceScore = confidence;
        this.conversationContext = context;
        this.processingTimeMs = processingTime;
        this.completedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Marks the request as failed with error message
     */
    public void markAsFailed(String errorMessage) {
        if (!isProcessing()) {
            throw new ChatbotException(
                "Cannot mark request as failed. Current status: " + status
            );
        }

        this.status = RequestStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Cancels the request
     */
    public void cancel() {
        if (status == RequestStatus.COMPLETED || status == RequestStatus.FAILED) {
            throw new ChatbotException(
                "Cannot cancel request. Current status: " + status
            );
        }
        this.status = RequestStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    /**
     * Checks if the confidence score is above acceptable threshold
     */
    public boolean hasHighConfidence() {
        return confidenceScore != null && confidenceScore >= 0.7;
    }

    /**
     * Checks if the confidence score is low and requires human intervention
     */
    public boolean requiresHumanIntervention() {
        return confidenceScore != null && confidenceScore < 0.5;
    }

    /**
     * Checks if the request is from a mobile channel
     */
    public boolean isMobileChannel() {
        return channelType == ChannelType.MOBILE_APP;
    }

    /**
     * Checks if the request is from a web channel
     */
    public boolean isWebChannel() {
        return channelType == ChannelType.WEB;
    }

    /**
     * Creates a domain event when request is created
     */
    public ChatbotRequestRequestedEvent createRequestedEvent() {
        return new ChatbotRequestRequestedEvent(
            UUID.fromString(id), tenantId, sessionId, channelType, createdAt
        );
    }

    /**
     * Creates a domain event when request is completed
     */
    public ChatbotRequestCompletedEvent createCompletedEvent() {
        return new ChatbotRequestCompletedEvent(
            UUID.fromString(id), tenantId, sessionId, intentType, confidenceScore,
            processingTimeMs, completedAt
        );
    }

    /**
     * Creates a domain event when request fails
     */
    public ChatbotRequestFailedEvent createFailedEvent() {
        return new ChatbotRequestFailedEvent(
            UUID.fromString(id), tenantId, sessionId, errorMessage, completedAt
        );
    }

    /**
     * Validates that the request has required data
     */
    public void validateRequest() {
        if (userMessage == null || userMessage.isBlank()) {
            throw new ChatbotException(
                "User message is required"
            );
        }
        if (sessionId == null || sessionId.isBlank()) {
            throw new ChatbotException(
                "Session ID is required"
            );
        }
        if (channelType == null) {
            throw new ChatbotException(
                "Channel type is required"
            );
        }
    }

    /**
     * Updates the conversation context
     */
    public void updateContext(String newContext) {
        this.conversationContext = newContext;
        this.updatedAt = Instant.now();
    }

    /**
     * Updates metadata information
     */
    public void updateMetadata(String metadata) {
        this.metadata = metadata;
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "ChatbotRequest{" +
                "id=" + id +
                ", tenantId='" + tenantId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", status=" + status +
                ", channelType=" + channelType +
                ", intentType=" + intentType +
                ", confidenceScore=" + confidenceScore +
                '}';
    }
}
