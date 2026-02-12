package com.gogidix.rapidassist.billing.service.infrastructure.persistence.memory;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.port.out.BillingAccountStore;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBillingAccountStore implements BillingAccountStore {

    private final ConcurrentHashMap<String, BillingAccount> byTenantId = new ConcurrentHashMap<>();

    @Override
    public Optional<BillingAccount> find(String tenantId) {
        return Optional.ofNullable(byTenantId.get(tenantId));
    }

    @Override
    public BillingAccount upsert(BillingAccount account) {
        byTenantId.put(account.tenantId(), account);
        return account;
    }
}
