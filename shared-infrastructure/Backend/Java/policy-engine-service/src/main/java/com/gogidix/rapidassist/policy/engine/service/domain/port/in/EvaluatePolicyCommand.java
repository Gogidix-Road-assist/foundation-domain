package com.gogidix.rapidassist.policy.engine.service.domain.port.in;

import com.gogidix.rapidassist.policy.engine.service.domain.model.PolicyDecision;

import java.util.Map;

public interface EvaluatePolicyCommand {

    PolicyDecision evaluate(String tenantId, String policyId, Map<String, Object> input);
}
