package com.gogidix.rapidassist.audit.correlation.service.application.usecase;

import com.gogidix.rapidassist.audit.correlation.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
