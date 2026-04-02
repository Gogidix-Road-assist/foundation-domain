package com.gogidix.rapidassist.identity.service.application.usecase;

import com.gogidix.rapidassist.identity.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
