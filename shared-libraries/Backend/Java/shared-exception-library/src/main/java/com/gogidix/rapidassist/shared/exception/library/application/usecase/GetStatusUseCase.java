package com.gogidix.rapidassist.shared.exception.library.application.usecase;

import com.gogidix.rapidassist.shared.exception.library.domain.port.in.GetStatusQuery;
import org.springframework.stereotype.Service;

@Service("exceptionGetStatusUseCase")
public class GetStatusUseCase implements GetStatusQuery {

    @Override
    public String getStatus() {
        return "OK";
    }
}
