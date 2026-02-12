package com.gogidix.rapidassist.ai.chatbot.infrastructure.messaging.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.chatbot.domain.event.*;
import com.gogidix.rapidassist.ai.chatbot.infrastructure.messaging.kafka.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka implementation of EventPublisher.
 * Publishes domain events to Kafka topics.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.chatbot.session.created:chatbot.session.created}")
    private String sessionCreatedTopic;

    @Value("${kafka.topic.chatbot.session.activated:chatbot.session.activated}")
    private String sessionActivatedTopic;

    @Value("${kafka.topic.chatbot.message.received:chatbot.message.received}")
    private String messageReceivedTopic;

    @Value("${kafka.topic.chatbot.session.completed:chatbot.session.completed}")
    private String sessionCompletedTopic;

    @Value("${kafka.topic.chatbot.session.terminated:chatbot.session.terminated}")
    private String sessionTerminatedTopic;

    public void publish(ChatbotSessionCreatedEvent event) {
        publishEvent(sessionCreatedTopic, event, "ChatbotSessionCreated");
    }

    public void publish(ChatbotSessionActivatedEvent event) {
        publishEvent(sessionActivatedTopic, event, "ChatbotSessionActivated");
    }

    public void publish(ChatbotMessageReceivedEvent event) {
        publishEvent(messageReceivedTopic, event, "ChatbotMessageReceived");
    }

    public void publish(ChatbotSessionCompletedEvent event) {
        publishEvent(sessionCompletedTopic, event, "ChatbotSessionCompleted");
    }

    public void publish(ChatbotSessionTerminatedEvent event) {
        publishEvent(sessionTerminatedTopic, event, "ChatbotSessionTerminated");
    }

    @Override
    public void publish(Object event) {
        if (event instanceof ChatbotSessionCreatedEvent) {
            publish((ChatbotSessionCreatedEvent) event);
        } else if (event instanceof ChatbotSessionActivatedEvent) {
            publish((ChatbotSessionActivatedEvent) event);
        } else if (event instanceof ChatbotMessageReceivedEvent) {
            publish((ChatbotMessageReceivedEvent) event);
        } else if (event instanceof ChatbotSessionCompletedEvent) {
            publish((ChatbotSessionCompletedEvent) event);
        } else if (event instanceof ChatbotSessionTerminatedEvent) {
            publish((ChatbotSessionTerminatedEvent) event);
        } else {
            log.warn("Unknown event type: {}", event.getClass().getSimpleName());
        }
    }

    /**
     * Generic method to publish events to Kafka.
     */
    private void publishEvent(String topic, Object event, String eventType) {
        try {
            String key = extractKey(event);
            String payload = objectMapper.writeValueAsString(event);

            log.info("Publishing {} event to topic {}: {}", eventType, topic, payload);

            CompletableFuture<SendResult<String, String>> future = 
                    kafkaTemplate.send(topic, key, payload);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published {} event to topic: {}", eventType, topic);
                } else {
                    log.error("Failed to publish {} event to topic: {}", eventType, topic, ex);
                }
            });

        } catch (Exception e) {
            log.error("Error serializing {} event: {}", eventType, event, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    /**
     * Extract key for Kafka message partitioning.
     */
    private String extractKey(Object event) {
        if (event instanceof ChatbotSessionCreatedEvent) {
            return ((ChatbotSessionCreatedEvent) event).getTenantId();
        } else if (event instanceof ChatbotSessionActivatedEvent) {
            return ((ChatbotSessionActivatedEvent) event).getTenantId();
        } else if (event instanceof ChatbotMessageReceivedEvent) {
            return ((ChatbotMessageReceivedEvent) event).getTenantId();
        } else if (event instanceof ChatbotSessionCompletedEvent) {
            return ((ChatbotSessionCompletedEvent) event).getTenantId();
        } else if (event instanceof ChatbotSessionTerminatedEvent) {
            return ((ChatbotSessionTerminatedEvent) event).getTenantId();
        }
        return "default";
    }
}
