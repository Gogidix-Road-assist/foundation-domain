package com.gogidix.rapidassist.country.localization.config.service.application.usecase;

import com.gogidix.rapidassist.country.localization.config.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
