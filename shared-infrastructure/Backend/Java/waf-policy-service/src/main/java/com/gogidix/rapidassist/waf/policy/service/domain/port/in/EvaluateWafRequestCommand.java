package com.gogidix.rapidassist.waf.policy.service.domain.port.in;

import com.gogidix.rapidassist.waf.policy.service.domain.model.WafDecision;

import java.util.Map;

public interface EvaluateWafRequestCommand {

    WafDecision evaluate(String tenantId, Map<String, Object> request);
}
