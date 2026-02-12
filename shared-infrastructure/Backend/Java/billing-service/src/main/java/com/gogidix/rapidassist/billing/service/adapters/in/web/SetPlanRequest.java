package com.gogidix.rapidassist.billing.service.adapters.in.web;

import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;
import jakarta.validation.constraints.NotNull;

public record SetPlanRequest(
        @NotNull BillingPlan plan
) {
}
