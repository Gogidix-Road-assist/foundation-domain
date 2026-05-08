package com.gogidix.rapidassist.audit.correlation.service.domain.port.in;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;

public interface UpsertCorrelationCommand {

    void upsert(CorrelationRecord record);
}
