package com.gogidix.rapidassist.ai.chatbot.infrastructure.messaging.kafka.event;

/**
 * Interface for publishing domain events to Kafka.
 */
public interface EventPublisher {
    /**
     * Publish a domain event.
     *
     * @param event the domain event to publish
     */
    void publish(Object event);
}
