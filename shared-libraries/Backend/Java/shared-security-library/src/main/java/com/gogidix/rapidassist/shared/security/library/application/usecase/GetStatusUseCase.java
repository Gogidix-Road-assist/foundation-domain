package com.gogidix.rapidassist.shared.security.library.application.usecase;

import com.gogidix.rapidassist.shared.security.library.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
