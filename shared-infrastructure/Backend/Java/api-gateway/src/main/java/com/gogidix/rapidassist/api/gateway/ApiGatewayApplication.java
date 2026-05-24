package com.gogidix.rapidassist.api.gateway;

import com.gogidix.rapidassist.shared.exception.library.autoconfigure.SharedExceptionAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;

@SpringBootApplication(exclude = {
    ServletWebServerFactoryAutoConfiguration.class,
    SharedExceptionAutoConfiguration.class
})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
