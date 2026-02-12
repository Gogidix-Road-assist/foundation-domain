package com.gogidix.rapidassist.courier.adapter.service.application.usecase;

import com.gogidix.rapidassist.courier.adapter.service.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
