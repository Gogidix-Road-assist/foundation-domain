package com.gogidix.rapidassist.waf.policy.service.adapters.in.web;

import java.util.Map;

public record EvaluateWafRequest(
        Map<String, Object> request
) {
}
