package com.gogidix.rapidassist.shared.idempotency.library.domain.policy;

import jakarta.servlet.http.HttpServletRequest;

public interface IdempotencyRequestHasher {

    String hash(HttpServletRequest request);
}
