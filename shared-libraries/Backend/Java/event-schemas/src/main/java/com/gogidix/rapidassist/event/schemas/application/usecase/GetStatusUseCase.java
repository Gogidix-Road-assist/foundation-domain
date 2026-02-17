package com.gogidix.rapidassist.event.schemas.application.usecase;

import com.gogidix.rapidassist.event.schemas.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service("eventSchemasGetStatusUseCase")
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
