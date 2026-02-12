package com.gogidix.rapidassist.billing.service.domain.port.in;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;

public interface SetBillingPlanCommand {

    BillingAccount setPlan(String tenantId, BillingPlan plan);
}
