package com.gogidix.rapidassist.policy.engine.service.application.usecase;

import com.gogidix.rapidassist.policy.engine.service.domain.model.PolicyDecision;
import com.gogidix.rapidassist.policy.engine.service.domain.port.in.EvaluatePolicyCommand;
import com.gogidix.rapidassist.policy.engine.service.domain.port.out.PolicyEvaluator;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EvaluatePolicyUseCase implements EvaluatePolicyCommand {

    private final PolicyEvaluator evaluator;

    public EvaluatePolicyUseCase(PolicyEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public PolicyDecision evaluate(String tenantId, String policyId, Map<String, Object> input) {
        return evaluator.evaluate(tenantId, policyId, input);
    }
}
