package com.gogidix.rapidassist.billing.service.application.usecase;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;
import com.gogidix.rapidassist.billing.service.domain.port.in.GetBillingAccountQuery;
import com.gogidix.rapidassist.billing.service.domain.port.out.BillingAccountStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class GetBillingAccountUseCase implements GetBillingAccountQuery {

    private final BillingAccountStore store;

    public GetBillingAccountUseCase(BillingAccountStore store) {
        this.store = store;
    }

    @Override
    public BillingAccount get(String tenantId) {
        return store.find(tenantId)
                .orElseGet(() -> store.upsert(new BillingAccount(tenantId, BillingPlan.FREE, Instant.now())));
    }
}
