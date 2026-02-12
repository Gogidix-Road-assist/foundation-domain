package com.gogidix.rapidassist.reporting.read.model.service.application.usecase;

import com.gogidix.rapidassist.reporting.read.model.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
