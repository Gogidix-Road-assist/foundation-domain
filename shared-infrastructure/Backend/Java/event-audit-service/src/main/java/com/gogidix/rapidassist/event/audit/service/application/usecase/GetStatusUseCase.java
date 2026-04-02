package com.gogidix.rapidassist.event.audit.service.application.usecase;

import com.gogidix.rapidassist.event.audit.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
