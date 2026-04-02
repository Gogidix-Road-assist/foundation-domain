package com.gogidix.rapidassist.identity.access.service.application.usecase;

import com.gogidix.rapidassist.identity.access.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
