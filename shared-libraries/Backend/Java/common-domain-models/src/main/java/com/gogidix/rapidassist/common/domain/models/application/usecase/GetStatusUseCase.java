package com.gogidix.rapidassist.common.domain.models.application.usecase;

import com.gogidix.rapidassist.common.domain.models.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
