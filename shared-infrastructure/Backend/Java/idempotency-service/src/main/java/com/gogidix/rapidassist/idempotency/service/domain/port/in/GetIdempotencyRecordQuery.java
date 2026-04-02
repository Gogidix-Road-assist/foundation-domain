package com.gogidix.rapidassist.idempotency.service.domain.port.in;

import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyRecord;

public interface GetIdempotencyRecordQuery {

    IdempotencyRecord get(String tenantId, String key);
}
