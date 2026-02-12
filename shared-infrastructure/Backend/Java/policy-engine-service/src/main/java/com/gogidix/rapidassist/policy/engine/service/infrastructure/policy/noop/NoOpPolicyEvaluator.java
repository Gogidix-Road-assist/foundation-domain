package com.gogidix.rapidassist.policy.engine.service.infrastructure.policy.noop;

import com.gogidix.rapidassist.policy.engine.service.domain.model.PolicyDecision;
import com.gogidix.rapidassist.policy.engine.service.domain.port.out.PolicyEvaluator;

import java.util.Map;

public class NoOpPolicyEvaluator implements PolicyEvaluator {

    @Override
    public PolicyDecision evaluate(String tenantId, String policyId, Map<String, Object> input) {
        return new PolicyDecision(false, policyId, "No policy evaluator configured", Map.of());
    }
}
