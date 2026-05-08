package com.gogidix.rapidassist.waf.policy.service.adapters.in.web;

import java.util.Map;

public record WafDecisionResponse(
        boolean allowed,
        String ruleId,
        String action,
        String reason,
        Map<String, Object> details
) {
}
