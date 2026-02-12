package com.gogidix.rapidassist.billing.service.application.usecase;

import com.gogidix.rapidassist.billing.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
