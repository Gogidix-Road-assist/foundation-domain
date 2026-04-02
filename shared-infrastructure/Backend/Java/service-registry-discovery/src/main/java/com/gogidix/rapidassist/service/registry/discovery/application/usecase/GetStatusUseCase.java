package com.gogidix.rapidassist.service.registry.discovery.application.usecase;

import com.gogidix.rapidassist.service.registry.discovery.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
