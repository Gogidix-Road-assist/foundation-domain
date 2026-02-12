package com.gogidix.rapidassist.shared.audit.library.infrastructure.noop;

import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope;
import com.gogidix.rapidassist.shared.audit.library.domain.port.out.AuditPublisher;

public class NoOpAuditPublisher implements AuditPublisher {

    @Override
    public void publish(AuditEventEnvelope event) {
    }
}
