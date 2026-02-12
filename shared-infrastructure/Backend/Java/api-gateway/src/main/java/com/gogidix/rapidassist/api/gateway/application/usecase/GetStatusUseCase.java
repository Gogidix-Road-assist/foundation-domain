package com.gogidix.rapidassist.api.gateway.application.usecase;

import com.gogidix.rapidassist.api.gateway.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
