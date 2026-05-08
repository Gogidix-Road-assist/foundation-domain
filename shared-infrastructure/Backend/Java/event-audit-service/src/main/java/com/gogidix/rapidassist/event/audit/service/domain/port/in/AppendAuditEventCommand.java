package com.gogidix.rapidassist.event.audit.service.domain.port.in;

import com.gogidix.rapidassist.event.audit.service.domain.model.AuditEvent;

public interface AppendAuditEventCommand {

    String append(AuditEvent event);
}
