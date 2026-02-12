package com.gogidix.rapidassist.anti.fraud.signals.service.application.usecase;

import com.gogidix.rapidassist.anti.fraud.signals.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
