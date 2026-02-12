package com.gogidix.rapidassist.billing.service.domain.model;

import java.time.Instant;

public record BillingAccount(
        String tenantId,
        BillingPlan plan,
        Instant updatedAt
) {
}
