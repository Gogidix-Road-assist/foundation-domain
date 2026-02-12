package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.messaging.events;

import com.gogidix.rapidassist.orchestration.dispatching.domain.event.DispatchEvent;

/**
 * Generic event publisher interface
 */
public interface EventPublisher {

    void publish(DispatchEvent event);

    void publish(String eventType, Object payload);
}
