package com.gogidix.rapidassist.api.keys.service.application.usecase;

import com.gogidix.rapidassist.api.keys.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Component;

@Component
public class GetStatusHandler implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
