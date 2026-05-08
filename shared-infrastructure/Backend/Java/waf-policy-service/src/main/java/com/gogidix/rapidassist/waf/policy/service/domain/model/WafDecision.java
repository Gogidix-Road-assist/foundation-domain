package com.gogidix.rapidassist.waf.policy.service.domain.model;

import java.util.Map;

public record WafDecision(
        boolean allowed,
        String ruleId,
        String action,
        String reason,
        Map<String, Object> details
) {
}
