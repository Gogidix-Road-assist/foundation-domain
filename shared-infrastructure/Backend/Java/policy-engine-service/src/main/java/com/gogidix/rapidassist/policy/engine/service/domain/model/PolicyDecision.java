package com.gogidix.rapidassist.policy.engine.service.domain.model;

import java.util.Map;

public record PolicyDecision(
        boolean allowed,
        String policyId,
        String reason,
        Map<String, Object> obligations
) {
}
