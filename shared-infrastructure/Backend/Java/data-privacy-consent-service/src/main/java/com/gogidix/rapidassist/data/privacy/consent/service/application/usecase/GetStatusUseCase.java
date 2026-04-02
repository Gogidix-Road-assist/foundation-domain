package com.gogidix.rapidassist.data.privacy.consent.service.application.usecase;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
