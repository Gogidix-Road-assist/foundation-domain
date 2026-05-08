package com.gogidix.rapidassist.waf.policy.service.application.usecase;

import com.gogidix.rapidassist.waf.policy.service.domain.model.WafDecision;
import com.gogidix.rapidassist.waf.policy.service.domain.port.in.EvaluateWafRequestCommand;
import com.gogidix.rapidassist.waf.policy.service.domain.port.out.WafPolicyEngine;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EvaluateWafRequestUseCase implements EvaluateWafRequestCommand {

    private final WafPolicyEngine policyEngine;

    public EvaluateWafRequestUseCase(WafPolicyEngine policyEngine) {
        this.policyEngine = policyEngine;
    }

    @Override
    public WafDecision evaluate(String tenantId, Map<String, Object> request) {
        return policyEngine.evaluate(tenantId, request);
    }
}
