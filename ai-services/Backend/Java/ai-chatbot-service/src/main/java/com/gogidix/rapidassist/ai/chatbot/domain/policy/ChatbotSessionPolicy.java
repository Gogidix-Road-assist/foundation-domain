package com.gogidix.rapidassist.ai.chatbot.domain.policy;

import com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatMessage;
import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection;
import com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Business policy for chatbot session operations.
 * Enforces business rules and constraints.
 */
public class ChatbotSessionPolicy {

    private static final int MAX_MESSAGE_LENGTH = 4000;
    private static final int MAX_IDLE_TIME_MINUTES = 30;
    private static final int MAX_SESSION_DURATION_HOURS = 24;
    private static final int MAX_MESSAGES_PER_SESSION = 1000;

    /**
     * Policy: Check if session can accept new messages
     */
    public static boolean canAcceptMessage(ChatbotSession session) {
        return session.canAcceptMessages();
    }

    /**
     * Policy: Check if message content is valid
     */
    public static boolean isValidMessageContent(String content) {
        return content != null && 
               !content.trim().isEmpty() && 
               content.length() <= MAX_MESSAGE_LENGTH;
    }

    /**
     * Policy: Check if session is expired (idle for too long)
     */
    public static boolean isSessionExpired(ChatbotSession session) {
        return session.isIdle(MAX_IDLE_TIME_MINUTES);
    }

    /**
     * Policy: Check if session has exceeded maximum duration
     */
    public static boolean hasExceededMaxDuration(ChatbotSession session) {
        long durationHours = session.getSessionDurationSeconds() / 3600;
        return durationHours >= MAX_SESSION_DURATION_HOURS;
    }

    /**
     * Policy: Check if session has exceeded maximum message count
     */
    public static boolean hasExceededMaxMessages(ChatbotSession session) {
        return session.getMessageCount() >= MAX_MESSAGES_PER_SESSION;
    }

    /**
     * Policy: Check if session should be auto-completed
     */
    public static boolean shouldAutoComplete(ChatbotSession session) {
        return isSessionExpired(session) || 
               hasExceededMaxDuration(session) || 
               hasExceededMaxMessages(session);
    }

    /**
     * Policy: Validate message before adding to session
     */
    public static void validateMessage(ChatMessage message) {
        if (!ChatMessagePolicy.isValidContent(message.getContent())) {
            throw new IllegalArgumentException("Invalid message content");
        }
        if (message.getDirection() == null) {
            throw new IllegalArgumentException("Message direction is required");
        }
    }

    /**
     * Policy: Check if session transition is valid
     */
    public static boolean isValidTransition(SessionStatus from, SessionStatus to) {
        return switch (from) {
            case INITIALIZED -> to == SessionStatus.ACTIVE || to == SessionStatus.TERMINATED;
            case ACTIVE -> to == SessionStatus.PAUSED || to == SessionStatus.COMPLETED || to == SessionStatus.TERMINATED;
            case PAUSED -> to == SessionStatus.ACTIVE || to == SessionStatus.COMPLETED || to == SessionStatus.TERMINATED;
            case COMPLETED, TERMINATED -> false; // Terminal states
        };
    }

    /**
     * Policy: Get recommended action for session
     */
    public static String getRecommendedAction(ChatbotSession session) {
        if (shouldAutoComplete(session)) {
            return "AUTO_COMPLETE";
        }
        if (session.isIdle(MAX_IDLE_TIME_MINUTES / 2)) {
            return "SEND_REMINDER";
        }
        if (session.isActive()) {
            return "CONTINUE";
        }
        return "REVIEW";
    }

    /**
     * Policy: Check if session requires human intervention
     */
    public static boolean requiresHumanIntervention(ChatbotSession session) {
        // Check for error indicators in recent messages
        List<ChatMessage> recentMessages = session.getOutboundMessages().stream()
                .filter(msg -> msg.getTimestamp() != null && 
                             msg.getTimestamp().isAfter(LocalDateTime.now().minusMinutes(5)))
                .toList();

        return recentMessages.stream()
                .anyMatch(msg -> msg.getContent() != null && 
                                   (msg.getContent().contains("ERROR") || 
                                    msg.getContent().contains("HUMAN intervention required")));
    }

    /**
     * Policy: Calculate session health score (0-100)
     */
    public static int calculateHealthScore(ChatbotSession session) {
        int score = 100;

        // Deduct for long idle time
        long idleMinutes = session.getTimeSinceLastActivitySeconds() / 60;
        if (idleMinutes > 10) {
            score -= Math.min(20, (int) (idleMinutes / 5));
        }

        // Deduct for session age
        long durationHours = session.getSessionDurationSeconds() / 3600;
        if (durationHours > 12) {
            score -= Math.min(30, (int) (durationHours / 2));
        }

        // Deduct for message saturation
        int messageCount = session.getMessageCount();
        if (messageCount > 500) {
            score -= Math.min(40, (messageCount - 500) / 25);
        }

        return Math.max(0, score);
    }
}
