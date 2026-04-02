package com.gogidix.rapidassist.tenancy.configuration.service.application.usecase;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
