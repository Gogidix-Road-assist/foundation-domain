package com.gogidix.rapidassist.policy.engine.service.domain.port.out;

import com.gogidix.rapidassist.policy.engine.service.domain.model.PolicyDecision;

import java.util.Map;

public interface PolicyEvaluator {

    PolicyDecision evaluate(String tenantId, String policyId, Map<String, Object> input);
}
