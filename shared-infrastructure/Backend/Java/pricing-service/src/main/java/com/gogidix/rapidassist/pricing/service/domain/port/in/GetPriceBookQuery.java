package com.gogidix.rapidassist.pricing.service.domain.port.in;

import com.gogidix.rapidassist.pricing.service.domain.model.PriceBook;

public interface GetPriceBookQuery {

    PriceBook get(String tenantId);
}
