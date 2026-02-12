package com.gogidix.rapidassist.billing.service.adapters.in.web;

import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;

import java.time.Instant;

public record BillingAccountResponse(
        BillingPlan plan,
        Instant updatedAt
) {
}
