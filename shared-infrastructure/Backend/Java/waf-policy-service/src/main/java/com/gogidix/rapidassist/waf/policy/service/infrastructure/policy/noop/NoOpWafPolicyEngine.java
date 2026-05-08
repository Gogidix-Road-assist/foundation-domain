package com.gogidix.rapidassist.waf.policy.service.infrastructure.policy.noop;

import com.gogidix.rapidassist.waf.policy.service.domain.model.WafDecision;
import com.gogidix.rapidassist.waf.policy.service.domain.port.out.WafPolicyEngine;

import java.util.Map;

public class NoOpWafPolicyEngine implements WafPolicyEngine {

    @Override
    public WafDecision evaluate(String tenantId, Map<String, Object> request) {
        return new WafDecision(false, "noop", "BLOCK", "No WAF policy engine configured", Map.of());
    }
}
