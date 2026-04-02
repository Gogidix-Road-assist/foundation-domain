package com.gogidix.rapidassist.shared.exception.library.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SharedExceptionAutoConfiguration {

    @Bean
    public GlobalProblemDetailExceptionHandler globalProblemDetailExceptionHandler() {
        return new GlobalProblemDetailExceptionHandler();
    }
}
