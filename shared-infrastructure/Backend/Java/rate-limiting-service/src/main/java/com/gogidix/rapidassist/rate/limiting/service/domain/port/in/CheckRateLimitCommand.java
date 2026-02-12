package com.gogidix.rapidassist.rate.limiting.service.domain.port.in;

import com.gogidix.rapidassist.rate.limiting.service.domain.model.RateLimitDecision;

import java.time.Duration;

public interface CheckRateLimitCommand {

    RateLimitDecision check(String tenantId, String key, long limit, Duration window);
}
