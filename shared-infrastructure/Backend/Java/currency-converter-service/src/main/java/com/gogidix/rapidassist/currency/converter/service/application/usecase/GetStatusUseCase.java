package com.gogidix.rapidassist.currency.converter.service.application.usecase;

import com.gogidix.rapidassist.currency.converter.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
