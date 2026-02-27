package com.gogidix.rapidassist.access.control.service.domain.port.out;

import com.gogidix.rapidassist.access.control.service.domain.event.AccessDeniedEvent;
import com.gogidix.rapidassist.access.control.service.domain.event.PermissionGrantedEvent;
import com.gogidix.rapidassist.access.control.service.domain.event.PermissionRevokedEvent;

/**
 * Output Port: AuditEventPublisher
 *
 * Interface for publishing domain events.
 * This is a PORT in the hexagonal architecture - the domain depends on this
 * abstraction, not on a specific messaging implementation.
 *
 * Implementations will publish to Kafka, RabbitMQ, or other message brokers.
 */
public interface AuditEventPublisher {

    /**
     * Publish a permission granted event.
     *
     * @param event The event to publish
     */
    void publish(PermissionGrantedEvent event);

    /**
     * Publish a permission revoked event.
     *
     * @param event The event to publish
     */
    void publish(PermissionRevokedEvent event);

    /**
     * Publish an access denied event.
     *
     * @param event The event to publish
     */
    void publish(AccessDeniedEvent event);

    /**
     * Publish a generic domain event.
     *
     * @param eventType The event type
     * @param payload The event payload
     */
    void publish(String eventType, Object payload);
}
