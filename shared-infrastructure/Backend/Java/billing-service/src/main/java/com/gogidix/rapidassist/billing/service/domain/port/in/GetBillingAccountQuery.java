package com.gogidix.rapidassist.billing.service.domain.port.in;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;

public interface GetBillingAccountQuery {

    BillingAccount get(String tenantId);
}
