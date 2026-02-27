package com.gogidix.rapidassist.billing.service.domain.port.out;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;

import java.util.Optional;

public interface BillingAccountStore {

    Optional<BillingAccount> find(String tenantId);

    BillingAccount upsert(BillingAccount account);
}
