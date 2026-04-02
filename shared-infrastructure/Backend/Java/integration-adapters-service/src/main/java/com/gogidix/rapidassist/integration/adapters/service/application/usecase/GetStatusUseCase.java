package com.gogidix.rapidassist.integration.adapters.service.application.usecase;

import com.gogidix.rapidassist.integration.adapters.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
