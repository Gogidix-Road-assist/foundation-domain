package com.gogidix.rapidassist.notification.service.application.usecase;

import com.gogidix.rapidassist.notification.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
