package com.gogidix.rapidassist.waf.policy.service.application.usecase;

import com.gogidix.rapidassist.waf.policy.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
