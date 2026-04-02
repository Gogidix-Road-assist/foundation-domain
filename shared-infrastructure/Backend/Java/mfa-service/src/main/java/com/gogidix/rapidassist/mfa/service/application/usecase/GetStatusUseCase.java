package com.gogidix.rapidassist.mfa.service.application.usecase;

import com.gogidix.rapidassist.mfa.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
