package com.gogidix.rapidassist.shared.audit.library.domain.port.out;

import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope;

public interface AuditPublisher {
    void publish(AuditEventEnvelope event);
}
