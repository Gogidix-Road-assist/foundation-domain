package com.gogidix.rapidassist.access.control.service.infrastructure.messaging;

import com.gogidix.rapidassist.access.control.service.domain.event.AccessDeniedEvent;
import com.gogidix.rapidassist.access.control.service.domain.event.PermissionGrantedEvent;
import com.gogidix.rapidassist.access.control.service.domain.event.PermissionRevokedEvent;
import com.gogidix.rapidassist.access.control.service.domain.port.out.AuditEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka-disabled")
public class NoOpAuditEventPublisher implements AuditEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NoOpAuditEventPublisher.class);

    @Override
    public void publish(PermissionGrantedEvent event) {
        log.debug("Audit event (noop): PermissionGranted for tenant {}", event.getTenantId());
    }

    @Override
    public void publish(PermissionRevokedEvent event) {
        log.debug("Audit event (noop): PermissionRevoked for tenant {}", event.getTenantId());
    }

    @Override
    public void publish(AccessDeniedEvent event) {
        log.debug("Audit event (noop): AccessDenied for tenant {}", event.getTenantId());
    }

    @Override
    public void publish(String eventType, Object payload) {
        log.debug("Audit event (noop): {} - {}", eventType, payload);
    }
}
