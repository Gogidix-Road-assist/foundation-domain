package com.gogidix.rapidassist.billing.service.application.usecase;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;
import com.gogidix.rapidassist.billing.service.domain.port.in.SetBillingPlanCommand;
import com.gogidix.rapidassist.billing.service.domain.port.out.BillingAccountStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SetBillingPlanUseCase implements SetBillingPlanCommand {

    private final BillingAccountStore store;

    public SetBillingPlanUseCase(BillingAccountStore store) {
        this.store = store;
    }

    @Override
    public BillingAccount setPlan(String tenantId, BillingPlan plan) {
        return store.upsert(new BillingAccount(tenantId, plan, Instant.now()));
    }
}
