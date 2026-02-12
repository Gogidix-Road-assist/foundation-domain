package com.gogidix.rapidassist.policy.engine.service.adapters.in.web;

import java.util.Map;

public record PolicyEvaluationResponse(
        boolean allowed,
        String policyId,
        String reason,
        Map<String, Object> obligations
) {
}
