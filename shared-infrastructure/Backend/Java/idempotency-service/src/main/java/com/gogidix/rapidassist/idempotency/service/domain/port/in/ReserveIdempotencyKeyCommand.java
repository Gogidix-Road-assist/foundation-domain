package com.gogidix.rapidassist.idempotency.service.domain.port.in;

import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyRecord;

public interface ReserveIdempotencyKeyCommand {

    IdempotencyRecord reserve(String tenantId, String key);
}
