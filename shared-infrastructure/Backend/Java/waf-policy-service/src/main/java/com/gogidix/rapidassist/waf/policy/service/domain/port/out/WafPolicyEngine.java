package com.gogidix.rapidassist.waf.policy.service.domain.port.out;

import com.gogidix.rapidassist.waf.policy.service.domain.model.WafDecision;

import java.util.Map;

public interface WafPolicyEngine {

    WafDecision evaluate(String tenantId, Map<String, Object> request);
}
