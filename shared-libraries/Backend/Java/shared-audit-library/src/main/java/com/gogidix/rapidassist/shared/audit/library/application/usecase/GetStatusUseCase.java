package com.gogidix.rapidassist.shared.audit.library.application.usecase;

import com.gogidix.rapidassist.shared.audit.library.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service("auditGetStatusUseCase")
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
