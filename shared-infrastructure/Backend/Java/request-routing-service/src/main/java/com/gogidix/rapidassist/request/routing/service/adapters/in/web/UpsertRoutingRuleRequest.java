package com.gogidix.rapidassist.request.routing.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record UpsertRoutingRuleRequest(
        @NotBlank String routeKey,
        @NotBlank String destinationBaseUrl
) {
}
