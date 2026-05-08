package com.gogidix.rapidassist.config.service.application.usecase;

import com.gogidix.rapidassist.config.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
