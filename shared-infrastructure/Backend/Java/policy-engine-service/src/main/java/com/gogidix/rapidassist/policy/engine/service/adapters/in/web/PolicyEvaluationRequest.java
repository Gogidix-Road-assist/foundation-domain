package com.gogidix.rapidassist.policy.engine.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record PolicyEvaluationRequest(
        @NotBlank String policyId,
        Map<String, Object> input
) {
}
