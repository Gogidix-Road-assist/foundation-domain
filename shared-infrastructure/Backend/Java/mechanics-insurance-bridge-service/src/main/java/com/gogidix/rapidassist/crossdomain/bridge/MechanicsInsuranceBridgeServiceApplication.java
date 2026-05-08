package com.gogidix.rapidassist.crossdomain.bridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MechanicsInsuranceBridgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MechanicsInsuranceBridgeServiceApplication.class, args);
    }
}
